package org.example.springboot02event.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * CommandLineRunner
 * 感知特定阶段：感知应用就绪Ready。卡死应用，就不会就绪
 * 配置方式:
 * 1.通过@Bean将监听器加入到容器中
 *
 *
 * @author booty
 */
@Component
@Slf4j
public class Listener6CommandLineRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.debug("=====触发监听器Listener6CommandLineRunner=====");
    }
}
