package com.example.event.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * ApplicationContextInitializer
 * 感知特定阶段：感知ioc容器初始化
 * 该接口的实现类会在ioc容器refresh之前被调用,也就是在ioc容器初始化之前被调用
 * 此时还不能使用ioc容器中的对象
 * 配置方式:
 * 1.通过META-INF/spring.factories配置
 * 2.通过main主程序对象application.addInitializers();方法添加
 * 3.通过main主程序流式构造器SpringApplicationBuilder.initializers();方法添加
 *
 * @author booty
 */
@Slf4j
public class Listener4ApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
//        Person bean = applicationContext.getBean(Person.class);
//        System.out.println(bean);
        /*
        若在此时获取容器中的对象,
        会抛出java.lang.IllegalStateException异常
        因为此时容器还未初始化完成
        org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext@34448e6c has not been refreshed yet
         */
        log.debug("=====触发监听器Listener4ApplicationContextInitializer=====");
    }
}
