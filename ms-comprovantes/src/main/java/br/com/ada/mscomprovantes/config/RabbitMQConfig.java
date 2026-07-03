package br.com.ada.mscomprovantes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_COMPROVANTES       = "comprovantes.queue";
    public static final String EXCHANGE_COMPROVANTES    = "comprovantes.exchange";
    public static final String ROUTING_KEY_COMPROVANTES = "comprovantes.routingkey";

    @Bean
    public Queue comprovanteQueue() {
        return QueueBuilder.durable(QUEUE_COMPROVANTES).build();
    }

    @Bean
    public DirectExchange comprovanteExchange() {
        return new DirectExchange(EXCHANGE_COMPROVANTES);
    }

    @Bean
    public Binding comprovanteBinding(Queue comprovanteQueue, DirectExchange comprovanteExchange) {
        return BindingBuilder
                .bind(comprovanteQueue)
                .to(comprovanteExchange)
                .with(ROUTING_KEY_COMPROVANTES);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}
