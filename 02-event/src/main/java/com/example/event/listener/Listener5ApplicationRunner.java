package com.example.event.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * ApplicationRunner:
 * 感知特定阶段：感知应用就绪Ready。卡死应用，就不会就绪
 * 配置方式:
 * 1.通过@Bean将监听器加入到容器中
 *
 * @author booty
 */
@Slf4j
@Component
public class Listener5ApplicationRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.debug("=====触发监听器Listener5ApplicationRunner=====");
    }
}
