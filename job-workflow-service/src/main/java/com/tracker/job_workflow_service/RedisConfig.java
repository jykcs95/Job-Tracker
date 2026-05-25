package com.tracker.job_workflow_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// '@Configuration' marks this class as a source of bean definitions for the application context.
@Configuration
public class RedisConfig {

    // Creates the RedisTemplate helper bean used to interact with our Redis Docker
    // cache.
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // Connect this helper template to our live Docker Redis instance (Port 6379)
        template.setConnectionFactory(connectionFactory);

        // KEYS & HASH KEYS: Formatted as clean, plain text Strings (e.g.,
        // "user:101:kanban")
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // VALUES & HASH VALUES: Formatted securely as universal JSON strings.
        // FIX: The new class requires using the modern '.builder().build()'
        // initialization pattern.
        GenericJacksonJsonRedisSerializer jsonSerializer = GenericJacksonJsonRedisSerializer.builder().build();

        // Apply our fresh, non-deprecated JSON serializer to the template configuration
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        return template;
    }
}