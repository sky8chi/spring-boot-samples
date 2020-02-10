package com.kxfo.springboot.samples.distributed.jta.config;

import org.hsqldb.jdbc.pool.JDBCXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jta.bitronix.BitronixXADataSourceWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * TODO
 *
 * @author by tianxiang.chi
 * @date 2020-02-10 12:04
 */
@Configuration
public class HsqlDbConfig {
    @Bean("dataSourceAccount")
    public DataSource dataSource() throws Exception {
        return createHsqlXADatasource("jdbc:hsqldb:mem:accountDb");
    }

    @Bean("dataSourceAudit")
    public DataSource dataSourceAudit() throws Exception {
        return createHsqlXADatasource("jdbc:hsqldb:mem:auditDb");
    }

    private DataSource createHsqlXADatasource(String connectionUrl)
            throws Exception {
        JDBCXADataSource dataSource = new JDBCXADataSource();
        dataSource.setUrl(connectionUrl);
        dataSource.setUser("sa");
        BitronixXADataSourceWrapper wrapper = new BitronixXADataSourceWrapper();
        return wrapper.wrapDataSource(dataSource);
    }

    @Bean("jdbcTemplateAccount")
    public JdbcTemplate jdbcTemplate(
            @Qualifier("dataSourceAccount") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean("jdbcTemplateAudit")
    public JdbcTemplate jdbcTemplateAudit(
            @Qualifier("dataSourceAudit") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
