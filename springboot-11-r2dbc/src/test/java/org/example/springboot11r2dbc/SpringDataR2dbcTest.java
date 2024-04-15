package org.example.springboot11r2dbc;


import org.example.springboot11r2dbc.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import java.util.concurrent.TimeUnit;

/**
 * SpringBoot 对r2dbc的自动配置
 * 1、R2dbcAutoConfiguration:   主要配置连接工厂、连接池
 *
 * 2、R2dbcDataAutoConfiguration： 主要给用户提供了 R2dbcEntityTemplate 可以进行CRUD操作
 *      R2dbcEntityTemplate: 操作数据库的响应式客户端；提供CruD api ; RedisTemplate XxxTemplate
 *      数据类型映射关系、转换器、自定义R2dbcCustomConversions 转换器组件
 *      数据类型转换：int，Integer；  varchar，String；  datetime，Instant
 *
 *
 *
 * 3、R2dbcRepositoriesAutoConfiguration： 开启Spring Data声明式接口方式的CRUD；
 *      mybatis-plus： 提供了 BaseMapper，IService；自带了CRUD功能；
 *      Spring Data：  提供了基础的CRUD接口，不用写任何实现的情况下，可以直接具有CRUD功能；
 *
 *
 * 4、R2dbcTransactionManagerAutoConfiguration： 事务管理
 *
 * @author booty
 */
@SpringBootTest
public class SpringDataR2dbcTest {

    @Autowired
    R2dbcEntityTemplate template;


    /**
     * id生成策略
     *
     * Spring Data 使用 identifer 属性来标识实体。实体的ID必须用Spring Data的@Id注解来注解。
     * 当数据库的 ID 列有自动增量列时，生成的值将在将其插入数据库后在实体中设置。
     * 当实体是新的且标识符值默认为其初始值时，Spring Data 不会尝试插入标识符列的值。这0适用于基元类型，并且null标识符属性使用数字包装类型,例如Long
     * 实体状态检测详细解释了检测实体是否是新实体或是否应该存在于数据库中的策略。
     * 一个重要的限制是，保存实体后，该实体不能再是新的。请注意，实体是否是新的是实体状态的一部分。
     * 对于自动增量列，这种情况会自动发生，因为 ID 是由 Spring Data 使用 ID 列中的值设置的。
     *
     * @author booty
     */
    @Test
    void idPolicy() throws Exception{
        Person person = new Person();
        person.setId(7L);
        person.setName("tianqi");
        template.insert(person).subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(3);
    }



}
