package org.example.springboot02event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
public class Springboot02EventApplication {

    public static void main(String[] args) {
        // 1.默认启动SpringBoot应用
//         SpringApplication.run(Application.class, args);


        // 2.启动后配置属性(优先级低于配置文件)
//        SpringApplication application = new SpringApplication(Application.class);
//        application.setBannerMode(Banner.Mode.OFF); // 关闭banner图打印   banner生成网址:https://patorjk.com/software/taag/  https://www.bootschool.net/ascii
//        application.run(args);


        // 3.FluentBuilderAPI,流式配置属性(优先级低于配置文件)
        ConfigurableApplicationContext context = new SpringApplicationBuilder()
                .sources(Springboot02EventApplication.class) // 必传
                .main(Springboot02EventApplication.class) //必传
//                .bannerMode(Banner.Mode.OFF) // 设置关闭banner
//                .addBootstrapRegistryInitializer(new Listener1BootstrapRegistryInitializer()) // 设置引导初始化器
//                .initializers(new Listener2ApplicationContextInitializer()) // 设置应用上下文初始化器
//                .listeners(new Listener3ApplicationListener()) // 设置应用监听器
                .run(args);
    }
}
