package com.example.controller;

import com.example.entity.User;
import com.example.event.LoginEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author booty
 */
@RestController
public class TestController {


    @Autowired
    private ApplicationContext context;


    @RequestMapping("/test")
    public String test(String username, String password){
        /*
        事件订阅发布机制,使用场景:
        例如, 当用户登录时,需要进行一些操作
        1.用户登录成功后，发送用户邮件通知用户异地登录
        2.用户登录成功后，记录登录日志
        3.用户登录成功后, 给用户增加积分
        按照原编码方式, 每次添加操作都要在登录方法中添加, 代码耦合度高, 且不易维护
        使用事件订阅发布机制, 只需要在登录方法中发布事件, 其他操作在监听器中进行即可
         */

        // 1、创建事件
        User user = new User().setUsername(username).setPassword(password);
        LoginEvent loginEvent = new LoginEvent(user);
        // 2、发布事件
        context.publishEvent(loginEvent);
        return "test";
    }



}
