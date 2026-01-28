package com.infra.mo.skt_giphttp_mo.config.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.orm.jpa.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
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
        DataSourceProperties props = altibaseJPADataSourceProperties();
        
        // driverClassName이 null인 경우 기본값 사용
        String driverClassName = props.getDriverClassName();
        if (driverClassName == null || driverClassName.trim().isEmpty()) {
            driverClassName = "Altibase.jdbc.driver.AltibaseDriver";
            LoggerFactory.getLogger(AltibaseJPADatabaseConfig.class).warn(
                "spring.datasource.altibase.jpa.driver-class-name이 설정되지 않아 기본값을 사용합니다: {}", driverClassName
            );
        }
        
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(props.getUrl());
        hikariConfig.setUsername(props.getUsername());
        hikariConfig.setPassword(props.getPassword());
        
        // HikariCP 최적화 설정 (소켓 고갈 방지)
        hikariConfig.setMaximumPoolSize(30);
        hikariConfig.setMinimumIdle(5);
        hikariConfig.setConnectionTimeout(30000);  // 30초
        hikariConfig.setIdleTimeout(600000);       // 10분
        hikariConfig.setMaxLifetime(1800000);      // 30분
        hikariConfig.setLeakDetectionThreshold(60000);  // 1분
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setValidationTimeout(5000);
        hikariConfig.setKeepaliveTime(300000);  // 5분마다 keepalive
        
        // Altibase 특화 설정
        hikariConfig.addDataSourceProperty("encoding", "UTF-16");
        
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        
        // Connection 래퍼 적용 (Read-only 경고 방지 및 소켓 누수 감지)
        return new DelegatingDataSource(hikariDataSource) {
            @Override
            public Connection getConnection() throws SQLException {
                return new ConnectionWrapper(super.getConnection());
            }
            
            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return new ConnectionWrapper(super.getConnection(username, password));
            }
        };
    }
    
    // Connection 래퍼 클래스 - setReadOnly 호출을 무시하고 소켓 누수 감지
    private static class ConnectionWrapper implements Connection {
        private static final Logger log = LoggerFactory.getLogger(ConnectionWrapper.class);
        private final Connection delegate;
        private final long createdTime = System.currentTimeMillis();
        private volatile boolean closed = false;
        
        public ConnectionWrapper(Connection delegate) {
            this.delegate = delegate;
        }
        
        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            // Altibase는 Read-only 연결을 지원하지 않으므로 무시
        }
        
        @Override
        public void close() throws SQLException {
            if (closed) {
                return;  // 이미 닫힌 경우 중복 close 방지
            }
            
            try {
                long holdTime = System.currentTimeMillis() - createdTime;
                if (holdTime > 60000) {  // 1분 이상 점유된 경우 경고
                    log.warn("Connection이 {}ms 동안 점유되었습니다. 소켓 누수 가능성 확인 필요.", holdTime);
                }
                
                if (!delegate.isClosed()) {
                    delegate.close();
                }
                closed = true;
            } catch (SQLException e) {
                log.error("Connection close 실패: {}", e.getMessage(), e);
                throw e;
            }
        }
        
        @Override
        public boolean isClosed() throws SQLException {
            return closed || delegate.isClosed();
        }
        
        @Override
        public boolean isReadOnly() throws SQLException {
            return delegate.isReadOnly();
        }
        
        // 나머지 Connection 메서드들은 delegate로 위임
        @Override
        public java.sql.DatabaseMetaData getMetaData() throws SQLException {
            return delegate.getMetaData();
        }
        
        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            delegate.setAutoCommit(autoCommit);
        }
        
        @Override
        public boolean getAutoCommit() throws SQLException {
            return delegate.getAutoCommit();
        }
        
        @Override
        public void commit() throws SQLException {
            delegate.commit();
        }
        
        @Override
        public void rollback() throws SQLException {
            delegate.rollback();
        }
        
        @Override
        public java.sql.Statement createStatement() throws SQLException {
            return delegate.createStatement();
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException {
            return delegate.prepareStatement(sql);
        }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql) throws SQLException {
            return delegate.prepareCall(sql);
        }
        
        @Override
        public String nativeSQL(String sql) throws SQLException {
            return delegate.nativeSQL(sql);
        }
        
        @Override
        public void setCatalog(String catalog) throws SQLException {
            delegate.setCatalog(catalog);
        }
        
        @Override
        public String getCatalog() throws SQLException {
            return delegate.getCatalog();
        }
        
        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            delegate.setTransactionIsolation(level);
        }
        
        @Override
        public int getTransactionIsolation() throws SQLException {
            return delegate.getTransactionIsolation();
        }
        
        @Override
        public java.sql.SQLWarning getWarnings() throws SQLException {
            return delegate.getWarnings();
        }
        
        @Override
        public void clearWarnings() throws SQLException {
            delegate.clearWarnings();
        }
        
        @Override
        public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return delegate.createStatement(resultSetType, resultSetConcurrency);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return delegate.prepareCall(sql, resultSetType, resultSetConcurrency);
        }
        
        @Override
        public java.util.Map<String, Class<?>> getTypeMap() throws SQLException {
            return delegate.getTypeMap();
        }
        
        @Override
        public void setTypeMap(java.util.Map<String, Class<?>> map) throws SQLException {
            delegate.setTypeMap(map);
        }
        
        @Override
        public void setHoldability(int holdability) throws SQLException {
            delegate.setHoldability(holdability);
        }
        
        @Override
        public int getHoldability() throws SQLException {
            return delegate.getHoldability();
        }
        
        @Override
        public java.sql.Savepoint setSavepoint() throws SQLException {
            return delegate.setSavepoint();
        }
        
        @Override
        public java.sql.Savepoint setSavepoint(String name) throws SQLException {
            return delegate.setSavepoint(name);
        }
        
        @Override
        public void rollback(java.sql.Savepoint savepoint) throws SQLException {
            delegate.rollback(savepoint);
        }
        
        @Override
        public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException {
            delegate.releaseSavepoint(savepoint);
        }
        
        @Override
        public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return delegate.prepareStatement(sql, autoGeneratedKeys);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return delegate.prepareStatement(sql, columnIndexes);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return delegate.prepareStatement(sql, columnNames);
        }
        
        @Override
        public java.sql.Clob createClob() throws SQLException {
            return delegate.createClob();
        }
        
        @Override
        public java.sql.Blob createBlob() throws SQLException {
            return delegate.createBlob();
        }
        
        @Override
        public java.sql.NClob createNClob() throws SQLException {
            return delegate.createNClob();
        }
        
        @Override
        public java.sql.SQLXML createSQLXML() throws SQLException {
            return delegate.createSQLXML();
        }
        
        @Override
        public boolean isValid(int timeout) throws SQLException {
            return delegate.isValid(timeout);
        }
        
        @Override
        public void setClientInfo(String name, String value) throws java.sql.SQLClientInfoException {
            delegate.setClientInfo(name, value);
        }
        
        @Override
        public void setClientInfo(java.util.Properties properties) throws java.sql.SQLClientInfoException {
            delegate.setClientInfo(properties);
        }
        
        @Override
        public String getClientInfo(String name) throws SQLException {
            return delegate.getClientInfo(name);
        }
        
        @Override
        public java.util.Properties getClientInfo() throws SQLException {
            return delegate.getClientInfo();
        }
        
        @Override
        public java.sql.Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return delegate.createArrayOf(typeName, elements);
        }
        
        @Override
        public java.sql.Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return delegate.createStruct(typeName, attributes);
        }
        
        @Override
        public void setSchema(String schema) throws SQLException {
            delegate.setSchema(schema);
        }
        
        @Override
        public String getSchema() throws SQLException {
            return delegate.getSchema();
        }
        
        @Override
        public void abort(java.util.concurrent.Executor executor) throws SQLException {
            delegate.abort(executor);
        }
        
        @Override
        public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) throws SQLException {
            delegate.setNetworkTimeout(executor, milliseconds);
        }
        
        @Override
        public int getNetworkTimeout() throws SQLException {
            return delegate.getNetworkTimeout();
        }
        
        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return delegate.unwrap(iface);
        }
        
        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return delegate.isWrapperFor(iface);
        }
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean altibaseEntityManagerFactory(
            EntityManagerFactoryBuilder builder
    ) {
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.dialect", "com.infra.mo.skt_giphttp_mo.config.db.AltibaseDialect");
        
        // Altibase Read-only 연결 경고 해결
        jpaProperties.put("hibernate.connection.read_only", "false");
        jpaProperties.put("hibernate.connection.provider_disables_autocommit", "true");
        jpaProperties.put("hibernate.connection.isolation", "2");
        jpaProperties.put("hibernate.jdbc.lob.non_contextual_creation", "true");

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
        JpaTransactionManager transactionManager = new JpaTransactionManager(emf);
        transactionManager.setDataSource(altiDataSourceForJPA());
        return transactionManager;
    }
}
