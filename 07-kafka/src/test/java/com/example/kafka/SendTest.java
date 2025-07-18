package com.example.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.StopWatch;

import java.util.concurrent.CompletableFuture;

/**
 * 生产者测试
 * @author booty
 */
@SpringBootTest
class SendTest {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 测试1
     *
     * @author booty
     */
    @Test
    void test1(){
        /*
        关于kafkaTemplate.send()方法返回值
        在版本3.0中，以前返回ListenableFuture的方法已更改为返回CompletableFuture。
        为了便于迁移，2.9版本添加了一个使用CompletableFuture() 的方法，该方法提供了具有CompletableFuture返回类型的相同方法;
         */
        StopWatch watch = new StopWatch();
        watch.start();
        var futures = new CompletableFuture[10000];
        for (int i = 0; i < 10000; i++) {
            CompletableFuture<SendResult<String, Object>> send = stringTemplate.send("order", "order.create."+i, "订单创建了："+i);
            futures[i]=send;
        }
        CompletableFuture.allOf(futures).join();
        watch.stop();
        System.out.println("发送1w条数据至kafka总耗时(毫秒)："+watch.getTotalTimeMillis());
    }



    @Autowired
    private KafkaTemplate<String,Object> stringTemplate;
    @Autowired
    private KafkaTemplate<String,byte[]> kafkaTemplateByte;

    @Test
    void test2(){
        // 多模板测试
        System.out.println(kafkaTemplate);
        System.out.println(stringTemplate);
        System.out.println(kafkaTemplateByte);
        /*
        org.springframework.kafka.core.KafkaTemplate@16a35bd
        org.springframework.kafka.core.KafkaTemplate@16a35bd
        org.springframework.kafka.core.KafkaTemplate@633cc6b5
         */
    }

    @Test
    void test3(){
        // 发送消息
        kafkaTemplate.send("order","order.create","订单创建了");
    }



}
