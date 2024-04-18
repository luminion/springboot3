package org.example.springboot08security.config;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

/**
 * 自定义健康检查
 * 实现 HealthIndicator 接口来定制组件的健康状态对象（Health） 返回
 *
 * @author booty
 */
//@Component
public class HealthChecker extends AbstractHealthIndicator {


    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {

        if(1 == 1){
            //存活
            builder.up()
                    .withDetail("code","1000")
                    .withDetail("msg","alive")
                    .withDetail("data","详细数据")
                    .build();
        }else {
            //下线
            builder.down()
                    .withDetail("code","1001")
                    .withDetail("msg","die")
                    .withDetail("data","详细数据")
                    .build();
        }
    }
}
