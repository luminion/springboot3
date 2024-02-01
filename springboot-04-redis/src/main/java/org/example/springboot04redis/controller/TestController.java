package org.example.springboot04redis.controller;

import org.example.springboot04redis.entitiy.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author booty
 */
@RestController
public class TestController {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @GetMapping("/count")
    public String count(){
        Long count = redisTemplate.opsForValue().increment("count");
        //常见数据类型  k: v value可以有很多类型
        //string： 普通字符串 ： redisTemplate.opsForValue()
        //list:    列表：       redisTemplate.opsForList()
        //set:     集合:       redisTemplate.opsForSet()
        //zset:    有序集合:    redisTemplate.opsForZSet()
        //hash：   map结构：    redisTemplate.opsForHash()

        return "访问了【"+count+"】次";
    }


    @GetMapping("/num/{num:\\d+}")
    public String saveNum(@PathVariable Integer num){
        //1、序列化： 对象转为字符串方式
        redisTemplate.opsForValue().set("number",num);
        return "ok";
    }

    @GetMapping("/num")
    public Object getNum(){
        return  redisTemplate.opsForValue().get("number");
    }

    @GetMapping("/user/{age:\\d+}")
    public String saveUser(@PathVariable Integer age){
        //1、序列化： 对象转为字符串方式
        User user = new User();
        user.setAge(age);
        user.setName("张三");
        redisTemplate.opsForValue().set("user",user);
        return "ok";
    }

    @GetMapping("/user")
    public Object getUser(){
        return  redisTemplate.opsForValue().get("user");
    }



}
