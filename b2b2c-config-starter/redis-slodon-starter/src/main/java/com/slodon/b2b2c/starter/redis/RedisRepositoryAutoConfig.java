package com.slodon.b2b2c.starter.redis;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;

/**
 * Redis相关配置
 */
@Configuration(proxyBeanMethods = false)
@EnableRedisRepositories
@EnableConfigurationProperties(SlodonRedisProperties.class)
public class RedisRepositoryAutoConfig {

    @Resource
    private SlodonRedisProperties slodonRedisProperties;

    @Bean
    public RedisTemplate<String, Object> objectRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(resetRedisConnectionFactory(connectionFactory));
        //配置序列化规则
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


    /**
     * 使用自定义redis配置重置redis连接工厂
     * @param connectionFactory
     * @return
     */
    private LettuceConnectionFactory resetRedisConnectionFactory(RedisConnectionFactory connectionFactory){
        LettuceConnectionFactory lettuceConnectionFactory = (LettuceConnectionFactory) connectionFactory;
        RedisStandaloneConfiguration standaloneConfiguration = lettuceConnectionFactory.getStandaloneConfiguration();
        //采用自定义配置
        standaloneConfiguration.setDatabase(slodonRedisProperties.getDatabase());
        standaloneConfiguration.setHostName(slodonRedisProperties.getHost());
        standaloneConfiguration.setPort(slodonRedisProperties.getPort());
        standaloneConfiguration.setPassword(slodonRedisProperties.getPassword());
        //将配置同步到client
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }

}
