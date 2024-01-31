package com.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
public class ConfigApplication {

    public static void main(String[] args) {
        // 1.默认启动SpringBoot应用
//         SpringApplication.run(Application.class, args);


        // 2.启动后配置属性(优先级低于配置文件)
//        SpringApplication application = new SpringApplication(Application.class);
//        application.setBannerMode(Banner.Mode.OFF); // 关闭banner图打印   banner生成网址:https://patorjk.com/software/taag/  https://www.bootschool.net/ascii
//        application.run(args);


        // 3.FluentBuilderAPI,流式配置属性(优先级低于配置文件)
        ConfigurableApplicationContext context = new SpringApplicationBuilder()
                .sources(ConfigApplication.class) // 必传
                .main(ConfigApplication.class) //必传
                .bannerMode(Banner.Mode.OFF) // 设置关闭banner
                .run(args);
    }
}
