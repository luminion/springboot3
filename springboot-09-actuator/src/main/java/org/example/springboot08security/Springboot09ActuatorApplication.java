package org.example.springboot08security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 整合Prometheus+Grafana 完成线上应用指标监控系统
 * 1、改造SpringBoot应用，产生Prometheus需要的格式数据
 *   - 导入 micrometer-registry-prometheus
 * 2、部署java应用。在同一个机器内，访问 http://ip:9999/actuator/prometheus 就能得到指标数据
 * 3、修改prometheus配置文件，让他拉取某个应用的指标数据
 * 4、去grafana添加一个prometheus数据源，配置好prometheus地址
 *
 */
@SpringBootApplication
public class Springboot09ActuatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(Springboot09ActuatorApplication.class, args);
    }

}
