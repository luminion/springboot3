package com.example;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
//         SpringApplication.run(Application.class, args);


        // 获取容器对象, 配置属性(优先级低于配置文件)
        SpringApplication application = new SpringApplication(Application.class);
        // 关闭banner图打印   banner生成网址:https://patorjk.com/software/taag/  https://www.bootschool.net/ascii
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);


        // 流式配置
//        new SpringApplicationBuilder()
//                .sources(Application.class)
//                .main(Application.class)
//                .bannerMode(Banner.Mode.OFF)
//                .run(args);

    }
}
