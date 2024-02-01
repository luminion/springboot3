package org.example.springboot02event.service;

import org.example.springboot02event.event.LoginEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @author booty
 */
@Service
@Slf4j
@Order(2) //  使用order注解可以指定事件订阅接收的优先级
public class LogServiceByImpl implements ApplicationListener<LoginEvent> {


    @Override
    public void onApplicationEvent(LoginEvent event) {
          log.info("日志服务(接口实现)感知到登录事件,记录用户登录日志"+event.getSource());
    }
}
