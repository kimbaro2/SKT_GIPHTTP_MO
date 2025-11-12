package com.infra.mo.skt_giphttp_mo.config.db;

import Altibase.jdbc.driver.AltibaseDataSource; // Altibase JDBC 드라이버 클래스
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.infra.mo.skt_giphttp_mo.db.altibase.repository", // 📍 여기에 맞춤
        entityManagerFactoryRef = "altibaseEntityManagerFactory",
        transactionManagerRef = "altibaseTransactionManager"
)
public class AltibaseJPADatabaseConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.altibase.jpa")
    public DataSourceProperties altibaseJPADataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource altiDataSourceForJPA() {
        return altibaseJPADataSourceProperties()
                .initializeDataSourceBuilder()
                .type(AltibaseDataSource.class) // Altibase 전용 DataSource
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean altibaseEntityManagerFactory(
            EntityManagerFactoryBuilder builder
    ) {
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.dialect", "com.infra.mo.skt_giphttp_mo.config.db.AltibaseDialect"); // 또는 유사한 Dialect


        return builder
                .dataSource(altiDataSourceForJPA())
                .packages("com.infra.mo.skt_giphttp_mo.db.altibase.entity") // 📦 Entity 경로
                .persistenceUnit("altibase")
                .properties(jpaProperties) // 👈 추가
                .build();
    }

    @Bean
    public PlatformTransactionManager altibaseTransactionManager(
            @Qualifier("altibaseEntityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
