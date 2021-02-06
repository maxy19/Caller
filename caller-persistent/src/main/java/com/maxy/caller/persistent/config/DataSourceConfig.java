package com.maxy.caller.persistent.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


/**
 * HikariCP连接池配置
 * @author maxy
 */
@Configuration
@Log4j2
@MapperScan(basePackages= {"com.maxy.caller.persistent.mapper"},sqlSessionFactoryRef="asSqlSessionFactory")
public class DataSourceConfig {

    @Bean(name = "asDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public HikariDataSource getAssistantDataSource() {
        return new HikariDataSource();
    }

    @Bean(name = "asSqlSessionFactory")
    public SqlSessionFactory asSqlSessionFactory(@Qualifier("asDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "asTransactionManager")
    public DataSourceTransactionManager asTransactionManager(@Qualifier("asDataSource") DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "asSqlSessionTemplate")
    public SqlSessionTemplate asSqlSessionTemplate(@Qualifier("asSqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}