package com.kamruddin.modulith.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.integration.jdbc.lock.LockRepository;
import org.springframework.integration.support.locks.LockRegistry;

import javax.sql.DataSource;

@Configuration
public class DistributedLockConfig {
    
    @Value("${spring.application.name}")
    private String applicationName;
    
    // @Bean
    // public LockRepository lockRepository(DataSource dataSource) {
    //     DefaultLockRepository lockRepository = new DefaultLockRepository(dataSource);
    //     // Cast to int as setTimeToLive expects int, not long
    //     lockRepository.setTimeToLive((int)Duration.ofSeconds(30).toMillis());
    //     lockRepository.setPrefix(applicationName + "_");
    //     return lockRepository;
    // }
    
    // @Bean
    // public LockRegistry lockRegistry(LockRepository lockRepository) {
    //     return new JdbcLockRegistry(lockRepository);
    // }
}