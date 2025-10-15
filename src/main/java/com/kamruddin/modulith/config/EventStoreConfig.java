package com.kamruddin.modulith.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.DependsOn;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.time.Duration;

@Configuration
public class EventStoreConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private DataSource dataSource;

    /**
     * Create the tables needed for locking if they don't exist
     */
    @Bean(name = "schemaInitializer")
    public Object initializeSchema() {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            
            // Check if the default INT_LOCK table exists (uppercase, used by DefaultLockRepository)
            String checkIntLockSql = "SELECT COUNT(1) FROM information_schema.tables WHERE table_name = 'int_lock'";
            Integer intLockCount = jdbcTemplate.queryForObject(checkIntLockSql, Integer.class);
            
            if (intLockCount != null && intLockCount == 0) {
                // Create the default INT_LOCK table that Spring Integration expects
                jdbcTemplate.execute("CREATE TABLE INT_LOCK (\n" +
                    "    LOCK_KEY VARCHAR(36) NOT NULL PRIMARY KEY,\n" +
                    "    REGION VARCHAR(100) NOT NULL,\n" +
                    "    CLIENT_ID VARCHAR(36),\n" +
                    "    CREATED_DATE TIMESTAMP WITH TIME ZONE NOT NULL\n" +
                    ")");
            }
            
            // Drop the custom modulith_lock table if it exists, since we're using the default now
            String checkModulithLockSql = "SELECT COUNT(1) FROM information_schema.tables WHERE table_name = 'modulith_lock'";
            Integer modulithLockCount = jdbcTemplate.queryForObject(checkModulithLockSql, Integer.class);
            
            if (modulithLockCount != null && modulithLockCount > 0) {
                jdbcTemplate.execute("DROP TABLE modulith_lock");
            }
        } catch (Exception e) {
            // Log the error, but don't fail startup
            System.err.println("Error initializing schema: " + e.getMessage());
        }
        
        return new Object(); // Return a dummy object since @Bean requires a return value
    }

    @Bean
    @Primary
    @DependsOn("schemaInitializer")
    public DefaultLockRepository lockRepository() {
        // Use default constructor without specifying a table name
        // This will use the default INT_LOCK table name
        DefaultLockRepository lockRepository = new DefaultLockRepository(dataSource);
        lockRepository.setTimeToLive((int)Duration.ofMinutes(5).toMillis());
        return lockRepository;
    }

    @Bean
    @DependsOn("schemaInitializer")
    public JdbcLockRegistry lockRegistry(DefaultLockRepository lockRepository) {
        return new JdbcLockRegistry(lockRepository);
    }
}