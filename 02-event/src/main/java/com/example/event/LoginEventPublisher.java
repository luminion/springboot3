package com.example.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * 事件发布者
 * 本质上是使用ApplicationEventPublisher的publishEvent方法发布事件
 * 实际上, ApplicationContext实现了ApplicationEventPublisher接口,
 * 所以实际编码时, 可以直接注入ApplicationEventPublisher或ApplicationContext, 并使用publishEvent方法发布事件
 *
 * @author booty
 */
@Component
public class LoginEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher=applicationEventPublisher;
    }

    public void publishEvent(LoginEvent loginEvent){
        applicationEventPublisher.publishEvent(loginEvent);
    }

}
