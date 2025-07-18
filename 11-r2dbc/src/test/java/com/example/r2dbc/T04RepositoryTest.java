package com.example.r2dbc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 * 使用org.springframework.data.repository.reactive.ReactiveCrudRepository接口
 * 可以类似jpa的编程方式实现sql查询, 且返回值默认均为Flux<T>或者Mono<T>
 * 使用之前需要添加@EnableR2dbcRepositories开启该功能
 * 并创建自己的接口继承ReactiveCrudRepository,指定实体类和主键类型
 *
 * @author booty
 */
@SpringBootTest
public class T04RepositoryTest {



    @Autowired
    PersonRepository repository;

    /**
     * 查询所有
     *
     * @author booty
     */
    @Test
    void readsAllEntitiesCorrectly() throws InterruptedException {

        repository.findAll()
                .subscribe(System.out::println)
                ;
        TimeUnit.SECONDS.sleep(3);
    }

    /**
     * 根据名称查询
     *
     * @author booty
     */
    @Test
    void readsEntitiesByNameCorrectly() throws InterruptedException{
        repository.findByFirstname("zhangsan")
                .subscribe(System.out::println)
                ;
        TimeUnit.SECONDS.sleep(3);
    }
}
