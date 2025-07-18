package com.example.redis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class RedisApp {


    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;


    /**
     * list:    列表：       redisTemplate.opsForList()
     */
    @Test
    void test1(){
        String key = "list";
        redisTemplate.opsForList().leftPush(key,"1");
        redisTemplate.opsForList().leftPush(key,"2");
        redisTemplate.opsForList().leftPush(key,"3");
        Object pop = redisTemplate.opsForList().leftPop(key);
        Assertions.assertEquals("3",pop);
        System.out.println(pop);
    }

    /**
     * set:     集合:       redisTemplate.opsForSet()
     */
    @Test
    void test2(){

        String key = "set";
        //1、给集合中添加元素
        redisTemplate.opsForSet().add(key,"1","2","3","3");

        // 判断集合中是否存在元素
        Boolean b1 = redisTemplate.opsForSet().isMember(key, "2");
        System.out.println("判断集合中是否存在放入的2"+b1);
        Boolean b2 = redisTemplate.opsForSet().isMember(key, "5");
        System.out.println("判断集合中是否存在未放入的5"+b1);
    }


    /**
     * z set:    有序集合:    redisTemplate.opsForZSet()
     */
    @Test
    void test3(){
        String key = "zset";
        redisTemplate.opsForZSet().add(key,"张三",10.00);
        redisTemplate.opsForZSet().add(key,"李四",0.3);
        redisTemplate.opsForZSet().add(key,"王五",15.32);

        ZSetOperations.TypedTuple<Object> max = redisTemplate.opsForZSet().popMax(key);
        System.out.println(max.getValue() + "=max=>" + max.getScore());
        ZSetOperations.TypedTuple<Object> min = redisTemplate.opsForZSet().popMin(key);
        System.out.println(min.getValue() + "=min=>" + min.getScore());
    }

    /**
     * hash：   map结构：    redisTemplate.opsForHash()
     */
    @Test
    void test4(){
        String mapName = "amap";
        redisTemplate.opsForHash().put(mapName,"name","张三");
        redisTemplate.opsForHash().put(mapName,"age","18");
        System.out.println(redisTemplate.opsForHash().get(mapName, "name"));
    }


    @Test
    void test5(){
        String key = "ttl";
        redisTemplate.opsForValue().set(key,"booty",5, TimeUnit.SECONDS);
        int count = 1;
        while (true){
            try {
                Object o = redisTemplate.opsForValue().get(key);
                if (o==null){
                    System.out.println("已过期");
                    break;
                }
                System.out.println("第"+count+++"秒获取"+o);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
