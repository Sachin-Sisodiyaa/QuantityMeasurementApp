package com.app.quantitymeasurement.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class DataSourceFallbackConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceFallbackConfig.class);

    @Bean
    @Primary
    public DataSource dataSource() {

        //Try MySQL first
        HikariDataSource mysqlDataSource = new HikariDataSource();
        mysqlDataSource.setJdbcUrl(
                "jdbc:mysql://127.0.0.1:3306/quantitymeasurementapp" +
                "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata"
        );
        mysqlDataSource.setUsername("root");
        mysqlDataSource.setPassword("18052004");
        mysqlDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        mysqlDataSource.setMaximumPoolSize(20);
        mysqlDataSource.setMinimumIdle(5);
        mysqlDataSource.setConnectionTimeout(30000);

        try (Connection conn = mysqlDataSource.getConnection()) {
            log.info("Connected to MySQL successfully");
            return mysqlDataSource;

        } catch (Exception ex) {
            log.warn("MySQL not available, switching to H2", ex);
            return createH2Fallback();
        }
    }

    //H2 fallback
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
