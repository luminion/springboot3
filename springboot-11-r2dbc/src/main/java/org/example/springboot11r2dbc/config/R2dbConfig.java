package org.example.springboot11r2dbc.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.MySqlDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;

@EnableR2dbcRepositories //开启 R2dbc 仓库功能, 使用类似jpa的方式写sql
@Configuration
public class R2dbConfig {

    @Bean //替换容器中原来的
    @ConditionalOnMissingBean
    public R2dbcCustomConversions conversions(){
        //添加自定义类型转换器
        return R2dbcCustomConversions.of(MySqlDialect.INSTANCE,new FatherConverter());
    }


    //==============使用多数据源==============

    /*
    @EnableR2dbcRepositories(basePackages = "com.acme.mysql", entityOperationsRef = "mysqlR2dbcEntityOperations")
     */

//    @Bean
    public ConnectionFactory mysqlConnectionFactory() {
        return null;
    }
//    @Bean
    public R2dbcEntityOperations mysqlR2dbcEntityOperations(ConnectionFactory connectionFactory) {
        DatabaseClient databaseClient = DatabaseClient.create(connectionFactory);
        return new R2dbcEntityTemplate(databaseClient, MySqlDialect.INSTANCE);
    }

}
