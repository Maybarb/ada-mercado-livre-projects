package br.com.ada.mscomprovantes.service;

import br.com.ada.mscomprovantes.config.RabbitMQConfig;
import br.com.ada.mscomprovantes.config.RedisConfig;
import br.com.ada.mscomprovantes.domain.dto.ComprovanteDetalheDto;
import br.com.ada.mscomprovantes.domain.dto.ComprovanteRequestDto;
import br.com.ada.mscomprovantes.domain.dto.ComprovanteResponseDto;
import br.com.ada.mscomprovantes.domain.entity.Comprovante;
import br.com.ada.mscomprovantes.repository.ComprovanteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComprovanteService {

    // Prefixo das chaves no Redis para evitar colisão com outros serviços
    private static final String CACHE_KEY_PREFIX = "comprovante:";
    // Número máximo de tentativas de busca no banco antes de retornar 404
    private static final int MAX_RETRIES = 3;

    private final ComprovanteRepository comprovanteRepository;
    private final RedisTemplate<String, ComprovanteDetalheDto> comprovanteRedisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final RedisConfig redisConfig;

    // -----------------------------------------------------------------------
    // POST - Membro 2: recebe a requisição, valida e envia para fila RabbitMQ
    // -----------------------------------------------------------------------

    public ComprovanteResponseDto criarComprovante(ComprovanteRequestDto request) {
        UUID id = UUID.randomUUID();
        LocalDateTime dataHoraRequisicao = LocalDateTime.now();

        // Monta o payload que será persistido pelo consumer
        Comprovante comprovante = Comprovante.builder()
                .id(id)
                .nome(request.getNome())
                .tipoDocumento(request.getTipoDocumento())
                .numeroDocumento(request.getNumeroDocumento())
                .numeroAgencia(request.getNumeroAgencia())
                .numeroConta(request.getNumeroConta())
                .digitoVerificadorConta(request.getDigitoVerificadorConta())
                .valorTransacao(request.getValorTransacao())
                .tipoChavePixDestino(request.getTipoChavePixDestino())
                .chavePixDestino(request.getChavePixDestino())
                .nomeClienteDestino(request.getNomeClienteDestino())
                .identificacaoPix(request.getIdentificacaoPix())
                .dataHoraTransacao(request.getDataHoraTransacao())
                .dataHoraRequisicao(dataHoraRequisicao)
                .build();

        log.info("Publicando comprovante na fila RabbitMQ. id={}", id);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_COMPROVANTES,
                RabbitMQConfig.ROUTING_KEY_COMPROVANTES,
                comprovante
        );

        return ComprovanteResponseDto.builder()
                .identificadorComprovante(id)
                .dataHoraRequisicao(dataHoraRequisicao)
                .build();
    }

    // -----------------------------------------------------------------------
    // GET - Membro 3: busca com cache-aside + 3 retentativas antes do 404
    // -----------------------------------------------------------------------

    /**
     * Estratégia Cache-Aside:
     * 1. Tenta encontrar no Redis.
     * 2. Se não encontrar (cache miss), busca no banco de dados.
     * 3. Se encontrar no banco, salva no Redis com TTL e retorna.
     * 4. Repete até MAX_RETRIES tentativas antes de lançar exceção 404.
     *
     * @param id UUID do comprovante
     * @return ComprovanteDetalheDto
     * @throws ComprovanteNaoEncontradoException após 3 tentativas sem sucesso
     */
    public ComprovanteDetalheDto buscarPorId(UUID id) {
        String cacheKey = CACHE_KEY_PREFIX + id;

        for (int tentativa = 1; tentativa <= MAX_RETRIES; tentativa++) {
            log.info("Buscando comprovante. id={} tentativa={}/{}", id, tentativa, MAX_RETRIES);

            // --- Passo 1: consulta o cache Redis ---
            ComprovanteDetalheDto cached = comprovanteRedisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("Cache HIT. id={} tentativa={}", id, tentativa);
                return cached;
            }

            log.info("Cache MISS. id={} tentativa={} — consultando banco de dados.", id, tentativa);

            // --- Passo 2: consulta o banco de dados ---
            Optional<Comprovante> optional = comprovanteRepository.findById(id);

            if (optional.isPresent()) {
                ComprovanteDetalheDto dto = ComprovanteDetalheDto.fromEntity(optional.get());

                // --- Passo 3: popula o cache com TTL configurado ---
                comprovanteRedisTemplate.opsForValue().set(
                        cacheKey,
                        dto,
                        Duration.ofSeconds(redisConfig.getTtlSeconds())
                );
                log.info("Comprovante salvo no cache Redis. id={} ttl={}s", id, redisConfig.getTtlSeconds());

                return dto;
            }

            // Comprovante ainda não foi persistido pelo consumer — aguarda antes de tentar novamente
            if (tentativa < MAX_RETRIES) {
                log.warn("Comprovante não encontrado no banco. id={} aguardando antes da tentativa {}/{}",
                        id, tentativa + 1, MAX_RETRIES);
                aguardar(500L * tentativa); // backoff linear: 500ms, 1000ms
            }
        }

        // Esgotou as 3 tentativas sem encontrar
        log.error("Comprovante não encontrado após {} tentativas. id={}", MAX_RETRIES, id);
        throw new ComprovanteNaoEncontradoException(
                String.format("Comprovante com id '%s' não encontrado após %d tentativas.", id, MAX_RETRIES)
        );
    }

    private void aguardar(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Thread interrompida durante espera de retry. Continuando...");
        }
    }
}
