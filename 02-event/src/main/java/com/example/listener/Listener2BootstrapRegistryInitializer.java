package com.example.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.BootstrapRegistryInitializer;

/**
 *
 * BootstrapRegistryInitializer
 * 感知特定阶段：感知引导初始化
 * 配置方式:
 * 1.通过META-INF/spring.factories配置
 * 2.通过main主程序对象application.addBootstrapRegistryInitializer();方法添加
 * 3.通过main主程序流式构造器SpringApplicationBuilder.addBootstrapRegistryInitializer();方法添加
 *
 * 使用场景：进行密钥校对授权(例如程序使用期限一年,从服务器请求验证,验证不通过,退出程序)。
 * @author booty
 */
@Slf4j
public class Listener2BootstrapRegistryInitializer implements BootstrapRegistryInitializer {

    @Override
    public void initialize(BootstrapRegistry registry) {
        // 编写需要触发的逻辑
        log.debug("=====触发监听器Listener2BootstrapRegistryInitializer======");
    }
}
