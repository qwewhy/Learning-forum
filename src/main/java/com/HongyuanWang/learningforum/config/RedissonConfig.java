package com.HongyuanWang.learningforum.config;

import lombok.Data;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.redisson.config.Config;
import org.redisson.Redisson;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Redisson 配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig{

    private String host;

    private Integer port;

    private Integer database;

    private String password;


    @Bean
    public RedissonClient redissonClient() {
        // 创建配置
        Config config = new Config();
        // 使用单机模式
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setDatabase(database)
                .setPassword(password);
        // 创建RedissonClient
        return Redisson.create(config);
    }
}
