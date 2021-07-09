package com.slodon.b2b2c.starter.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis相关配置
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SlodonRedissonProperties.class)
public class RedissonLockAutoConfig {


    @Bean
    public SlodonLock slodonLock(SlodonRedissonProperties properties) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(properties.getHost() + ":" + properties.getPort())
                .setDatabase(properties.getDatabase())
                .setPassword(properties.getPassword());
        RedissonClient redissonClient = Redisson.create(config);
        return new SlodonLock(redissonClient);
    }

}
