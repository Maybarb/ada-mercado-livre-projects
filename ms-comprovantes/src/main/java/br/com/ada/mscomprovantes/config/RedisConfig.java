package br.com.ada.mscomprovantes.config;

import br.com.ada.mscomprovantes.domain.dto.ComprovanteDetalheDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${cache.redis.ttl-seconds:300}")
    private long ttlSeconds;

    /**
     * Template tipado: chave = UUID (String), valor = ComprovanteDetalheDto (JSON)
     */
    @Bean
    public RedisTemplate<String, ComprovanteDetalheDto> comprovanteRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<ComprovanteDetalheDto> valueSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, ComprovanteDetalheDto.class);

        RedisTemplate<String, ComprovanteDetalheDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(valueSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();

        return template;
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }
}
