package org.example.springboot02event.service;

import org.example.springboot02event.event.LoginEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 通过@EventListener注解实现事件监听
 * @author booty
 */
@Service
@Slf4j
public class LoginServiceAnno {

    @Async  // 异步执行
    @Order(1) // 使用order注解可以指定事件订阅接收的优先级,值越小优先级越高, 默认按照类名的字典顺序(注解方式为匿名类,优先级最低)
    @EventListener
    public void login(LoginEvent loginEvent){
       log.info("登录服务(注解方法)感知到登录事件,通知用户异地登录"+loginEvent.getSource());
    }
}
