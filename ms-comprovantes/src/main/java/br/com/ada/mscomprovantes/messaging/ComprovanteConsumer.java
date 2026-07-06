package br.com.ada.mscomprovantes.messaging;

import br.com.ada.mscomprovantes.config.RabbitMQConfig;
import br.com.ada.mscomprovantes.domain.entity.Comprovante;
import br.com.ada.mscomprovantes.repository.ComprovanteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ComprovanteConsumer {

    private static final Logger log = LoggerFactory.getLogger(ComprovanteConsumer.class);

    private final ComprovanteRepository comprovanteRepository;

    public ComprovanteConsumer(ComprovanteRepository comprovanteRepository) {
        this.comprovanteRepository = comprovanteRepository;
    }

    /**
     * Membro 2 — Consome a fila e persiste o comprovante no banco.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_COMPROVANTES)
    public void consumir(Comprovante comprovante) {
        log.info("Mensagem recebida da fila. Persistindo comprovante. id={}", comprovante.getId());
        comprovanteRepository.save(comprovante);
        log.info("Comprovante persistido com sucesso. id={}", comprovante.getId());
    }
}
