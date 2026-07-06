package br.com.ada.mscomprovantes.service;

import br.com.ada.mscomprovantes.config.RabbitMQConfig;
import br.com.ada.mscomprovantes.config.RedisConfig;
import br.com.ada.mscomprovantes.domain.dto.ComprovanteDetalheDto;
import br.com.ada.mscomprovantes.domain.dto.ComprovanteRequestDto;
import br.com.ada.mscomprovantes.domain.dto.ComprovanteResponseDto;
import br.com.ada.mscomprovantes.domain.entity.Comprovante;
import br.com.ada.mscomprovantes.repository.ComprovanteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ComprovanteService {

    private static final Logger log = LoggerFactory.getLogger(ComprovanteService.class);

    // Prefixo das chaves no Redis para evitar colisão com outros serviços
    private static final String CACHE_KEY_PREFIX = "comprovante:";
    // Número máximo de tentativas antes de retornar 404
    private static final int MAX_RETRIES = 3;

    private final ComprovanteRepository comprovanteRepository;
    private final RedisTemplate<String, ComprovanteDetalheDto> comprovanteRedisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final RedisConfig redisConfig;

    public ComprovanteService(ComprovanteRepository comprovanteRepository,
                              RedisTemplate<String, ComprovanteDetalheDto> comprovanteRedisTemplate,
                              RabbitTemplate rabbitTemplate,
                              RedisConfig redisConfig) {
        this.comprovanteRepository      = comprovanteRepository;
        this.comprovanteRedisTemplate   = comprovanteRedisTemplate;
        this.rabbitTemplate             = rabbitTemplate;
        this.redisConfig                = redisConfig;
    }

    // -----------------------------------------------------------------------
    // POST - Membro 2: recebe, valida e publica na fila RabbitMQ
    // -----------------------------------------------------------------------

    public ComprovanteResponseDto criarComprovante(ComprovanteRequestDto request) {
        UUID id = UUID.randomUUID();
        LocalDateTime dataHoraRequisicao = LocalDateTime.now();

        Comprovante comprovante = new Comprovante();
        comprovante.setId(id);
        comprovante.setNome(request.getNome());
        comprovante.setTipoDocumento(request.getTipoDocumento());
        comprovante.setNumeroDocumento(request.getNumeroDocumento());
        comprovante.setNumeroAgencia(request.getNumeroAgencia());
        comprovante.setNumeroConta(request.getNumeroConta());
        comprovante.setDigitoVerificadorConta(request.getDigitoVerificadorConta());
        comprovante.setValorTransacao(request.getValorTransacao());
        comprovante.setTipoChavePixDestino(request.getTipoChavePixDestino());
        comprovante.setChavePixDestino(request.getChavePixDestino());
        comprovante.setNomeClienteDestino(request.getNomeClienteDestino());
        comprovante.setIdentificacaoPix(request.getIdentificacaoPix());
        comprovante.setDataHoraTransacao(request.getDataHoraTransacao());
        comprovante.setDataHoraRequisicao(dataHoraRequisicao);

        log.info("Publicando comprovante na fila RabbitMQ. id={}", id);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_COMPROVANTES,
                RabbitMQConfig.ROUTING_KEY_COMPROVANTES,
                comprovante
        );

        return new ComprovanteResponseDto(id, dataHoraRequisicao);
    }

    // -----------------------------------------------------------------------
    // GET - Membro 3: cache-aside com até 3 tentativas antes do 404
    // -----------------------------------------------------------------------

    /**
     * Estratégia Cache-Aside:
     * 1. Busca no Redis pelo UUID.
     * 2. Cache HIT  → retorna imediatamente.
     * 3. Cache MISS → busca no banco de dados.
     * 4. Encontrou no banco → salva no Redis (com TTL) e retorna.
     * 5. Não encontrou → aguarda com backoff e tenta novamente.
     * 6. Após MAX_RETRIES (3) sem sucesso → lança 404.
     */
    public ComprovanteDetalheDto buscarPorId(UUID id) {
        String cacheKey = CACHE_KEY_PREFIX + id;

        for (int tentativa = 1; tentativa <= MAX_RETRIES; tentativa++) {
            log.info("Buscando comprovante. id={} tentativa={}/{}", id, tentativa, MAX_RETRIES);

            // Passo 1 — consulta o cache Redis
            ComprovanteDetalheDto cached = comprovanteRedisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("Cache HIT. id={}", id);
                return cached;
            }

            log.info("Cache MISS. id={} tentativa={} — consultando banco.", id, tentativa);

            // Passo 2 — consulta o banco de dados
            Optional<Comprovante> optional = comprovanteRepository.findById(id);

            if (optional.isPresent()) {
                ComprovanteDetalheDto dto = ComprovanteDetalheDto.fromEntity(optional.get());

                // Passo 3 — popula o cache com TTL configurado
                comprovanteRedisTemplate.opsForValue().set(
                        cacheKey,
                        dto,
                        Duration.ofSeconds(redisConfig.getTtlSeconds())
                );
                log.info("Comprovante salvo no Redis. id={} ttl={}s", id, redisConfig.getTtlSeconds());

                return dto;
            }

            // Comprovante ainda não foi persistido pelo consumer — backoff antes de retentar
            if (tentativa < MAX_RETRIES) {
                log.warn("Não encontrado no banco. id={} aguardando antes da tentativa {}/{}",
                        id, tentativa + 1, MAX_RETRIES);
                aguardar(500L * tentativa); // 500ms → 1000ms
            }
        }

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
            log.warn("Thread interrompida durante espera de retry.");
        }
    }
}
