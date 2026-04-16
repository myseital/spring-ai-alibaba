package org.alibaba.cloud.ai.agent.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author myseital
 * @date 2026/4/15
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private Integer port;

    @Value("${spring.data.redis.database}")
    private Integer database;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec());
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setDatabase(database);

        return Redisson.create(config);
    }
}
