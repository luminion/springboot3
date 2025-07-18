package com.example.event.event;

import com.example.event.entity.User;
import org.springframework.context.ApplicationEvent;

/**
 * 自定义登录事件
 *
 * 自定义事件的步骤:
 * 1.创建事件类，继承ApplicationEvent
 * 2.编写有参构造器，入参指定为自己所需要的类型参数
 * 3.编写Listener监听器，监听自定义类型的事件
 *
 * @author bootystar
 */
public class LoginEvent extends ApplicationEvent {

    /**
     * 登录事件构造器
     * 此处入参可自定义为指定类型参数
     * @param user 用户信息
     */
    public LoginEvent(User user) {
        super(user);
    }



}
