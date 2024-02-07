package com.alertmonitor.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String REDIS_HOST;

    @Value("${spring.redis.port}")
    private int REDIS_PORT;

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        final RedisTemplate<String, String> redisTemplate
                = new RedisTemplate<String, String>();

        redisTemplate.setKeySerializer(new StringRedisSerializer());

        redisTemplate.setHashKeySerializer(
                new GenericToStringSerializer<String>(String.class));

        redisTemplate.setHashValueSerializer(
                new StringRedisSerializer());

        redisTemplate.setValueSerializer(
                new StringRedisSerializer());

        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration(REDIS_HOST, REDIS_PORT);
//        configuration.setPassword("nanhe");

        JedisConnectionFactory factory = new JedisConnectionFactory(configuration);
        factory.afterPropertiesSet();
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }


}
