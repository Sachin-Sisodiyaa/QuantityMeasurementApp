package com.app.quantitymeasurement.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class DataSourceFallbackConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceFallbackConfig.class);

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password:}")
    private String datasourcePassword;

    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String datasourceDriverClassName;

    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private int maximumPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:2}")
    private int minimumIdle;

    @Value("${spring.datasource.hikari.connection-timeout:30000}")
    private long connectionTimeout;

    @Bean
    @Primary
    public DataSource dataSource() {

        // Try the configured database first. If it is unavailable locally, use H2 so
        // the app can still boot for development and tests.
        HikariDataSource mysqlDataSource = new HikariDataSource();
        mysqlDataSource.setJdbcUrl(datasourceUrl);
        mysqlDataSource.setUsername(datasourceUsername);
        mysqlDataSource.setPassword(datasourcePassword);
        mysqlDataSource.setDriverClassName(datasourceDriverClassName);

        mysqlDataSource.setMaximumPoolSize(maximumPoolSize);
        mysqlDataSource.setMinimumIdle(minimumIdle);
        mysqlDataSource.setConnectionTimeout(connectionTimeout);

        try (Connection conn = mysqlDataSource.getConnection()) {
            log.info("Connected to configured database successfully");
            return mysqlDataSource;

        } catch (Exception ex) {
            log.warn("Configured database not available, switching to H2", ex);
            return createH2Fallback();
        }
    }

    // H2 fallback
    private DataSource createH2Fallback() {
        HikariDataSource h2DataSource = new HikariDataSource();

        h2DataSource.setJdbcUrl(
                "jdbc:h2:mem:quantitymeasurementapp;" +
                "MODE=MySQL;" +
                "DB_CLOSE_DELAY=-1;" +
                "DATABASE_TO_UPPER=false"
        );
        h2DataSource.setDriverClassName("org.h2.Driver");
        h2DataSource.setUsername("sa");
        h2DataSource.setPassword("");

        h2DataSource.setMaximumPoolSize(10);
        h2DataSource.setMinimumIdle(2);

        log.info("H2 fallback database initialized");

        return h2DataSource;
    }
}
