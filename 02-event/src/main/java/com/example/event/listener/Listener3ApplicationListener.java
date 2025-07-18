package com.example.event.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * ApplicationListener
 * 感知全阶段：基于事件机制，感知事件。 一旦到了哪个阶段可以做别的事
 * 如果需要使用事件机制, 则推荐使用该监听器
 * 配置方式:
 * 1.通过META-INF/spring.factories配置
 * 2.通过main主程序对象application.addListeners();方法添加
 * 3.通过main主程序流式构造器SpringApplicationBuilder.listeners();方法添加
 * 4.通过@Bean将监听器加入到容器中
 * 5.在指定方法上添加@EventListener注解, 方法格式: void xxx(ApplicationEvent或ApplicationEvent的父类) (例:main启动类的中的onApplicationEvent方法)
 *
 * @author booty
 */
@Slf4j
public class Listener3ApplicationListener implements ApplicationListener<ApplicationEvent> {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.debug("=====触发监听器Listener3ApplicationListener=====, event: " + event.getClass().getSimpleName());
    }
}
