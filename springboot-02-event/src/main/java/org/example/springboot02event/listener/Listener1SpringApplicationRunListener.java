package org.example.springboot02event.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.Duration;

/**
 * SpringApplicationRunListener：
 * 感知全阶段生命周期 + 各种阶段都能自定义操作。
 * 配置方式:
 * 1.通过META-INF/spring.factories配置
 *
 *
 * @author booty
 */
@Slf4j
public class Listener1SpringApplicationRunListener implements SpringApplicationRunListener {

    private void print(String event){
        log.debug("=====触发监听器Listener1SpringApplicationRunListener======"+event);
    }


    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        // 应用开始，SpringApplication的run方法一调用，只要有了 BootstrapContext 就执行
        print("starting事件, 正在启动");
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        // 环境准备好（把启动参数等绑定到环境变量中），但是ioc还没有创建；【调一次】
        print("environmentPrepared事件, 环境准备完成");
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        // ioc容器创建并准备好，但是sources（主配置类）没加载。并关闭引导上下文；组件都没创建  【调一次】
        print("contextPrepared, ioc容器准备完成");
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        // ioc容器加载。主配置类加载进去了。但是ioc容器还没刷新（bean没创建）。
        print("contextLoaded事件, ioc容器加载完成");
    }

    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        // ioc容器刷新了（所有bean造好了），但是 runner 没调用。
        print("started事件, 启动完成");
    }

    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        // ioc容器刷新了（所有bean造好了），所有runner调用完了。
        print("ready事件, 应用已经准备就绪");

    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        // 以上任意步骤出错，都会调用,表示启动失败, 之后就不会再调用其他步骤了
        print("failed事件, 应用启动失败");
    }

    /*
    单次启动的打印结果:(可将日志等级调整为warn,即可只打印监听器的输出)
=====触发监听器Listener2BootstrapRegistryInitializer======
=====触发监听器Listener3ApplicationListener=====, event: ApplicationStartingEvent
=====触发监听器Listener1SpringApplicationRunListener======starting事件, 正在启动
=====触发监听器Listener3ApplicationListener=====, event: ApplicationEnvironmentPreparedEvent
=====触发监听器Listener1SpringApplicationRunListener======environmentPrepared事件, 环境准备完成
=====触发监听器Listener4ApplicationContextInitializer=====
=====触发监听器Listener3ApplicationListener=====, event: ApplicationContextInitializedEvent
=====触发监听器Listener1SpringApplicationRunListener======contextPrepared, ioc容器准备完成
=====触发监听器Listener3ApplicationListener=====, event: ApplicationPreparedEvent
=====触发监听器Listener1SpringApplicationRunListener======contextLoaded事件, ioc容器加载完成
=====触发监听器Listener3ApplicationListener=====, event: ServletWebServerInitializedEvent
=====触发监听器@EventListener的注解方法监听器===event: ServletWebServerInitializedEvent
=====触发监听器Listener3ApplicationListener=====, event: ContextRefreshedEvent
=====触发监听器@EventListener的注解方法监听器===event: ContextRefreshedEvent
=====触发监听器Listener3ApplicationListener=====, event: ApplicationStartedEvent
=====触发监听器@EventListener的注解方法监听器===event: ApplicationStartedEvent
=====触发监听器Listener3ApplicationListener=====, event: AvailabilityChangeEvent
=====触发监听器@EventListener的注解方法监听器===event: AvailabilityChangeEvent
=====触发监听器Listener1SpringApplicationRunListener======started事件, 启动完成
=====触发监听器Listener5ApplicationRunner=====
=====触发监听器Listener6CommandLineRunner=====
=====触发监听器Listener3ApplicationListener=====, event: ApplicationReadyEvent
=====触发监听器@EventListener的注解方法监听器===event: ApplicationReadyEvent
=====触发监听器Listener3ApplicationListener=====, event: AvailabilityChangeEvent
=====触发监听器@EventListener的注解方法监听器===event: AvailabilityChangeEvent
=====触发监听器Listener1SpringApplicationRunListener======ready事件, 应用已经准备就绪

     */


}
