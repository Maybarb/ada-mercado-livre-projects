package br.com.ada.mscomprovantes.messaging;

import br.com.ada.mscomprovantes.config.RabbitMQConfig;
import br.com.ada.mscomprovantes.domain.entity.Comprovante;
import br.com.ada.mscomprovantes.repository.ComprovanteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ComprovanteConsumer {

    private final ComprovanteRepository comprovanteRepository;

    /**
     * Membro 2 — Consome a fila RabbitMQ e persiste o comprovante no banco.
     * Após salvar, o evento de "Pagamento Realizado" seria publicado no Kafka
     * pelo Membro 4 (ver ms-notificacoes).
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_COMPROVANTES)
    public void consumir(Comprovante comprovante) {
        log.info("Mensagem recebida da fila. Persistindo comprovante. id={}", comprovante.getId());
        comprovanteRepository.save(comprovante);
        log.info("Comprovante persistido com sucesso. id={}", comprovante.getId());
    }
}
