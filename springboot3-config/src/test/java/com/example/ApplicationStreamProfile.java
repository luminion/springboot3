//package com.example;
//
//import com.example.entity.PersonProfileDev;
//import com.example.entity.PersonProfileDevTest;
//import com.example.entity.PersonProfileTest;
//import org.springframework.boot.Banner;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.context.ConfigurableApplicationContext;
//
//
//
///**
// * 环境隔离：
// * 任何@Component, @Configuration, @Bean,  @ConfigurationProperties 标记的类
// * 可以使用 @Profile 标记，来指定何时被加载。【容器中的组件都可以被 @Profile标记】
// *
// * 1、标识环境
// *    1）、区分出几个环境： dev（开发环境）、test（测试环境）、prod（生产环境）
// *    2）、指定每个组件在哪个环境下生效； default环境：默认环境
// *          通过： @Profile({"test"})标注
// *          组件没有标注@Profile代表任意时候都生效
// *    3）、默认只有激活指定的环境，这些组件才会生效。
// * 2、激活环境
// *    配置文件激活：spring.profiles.active=dev；
// *    命令行激活： java -jar xxx.jar  --spring.profiles.active=dev
// *
// * 3、配置文件怎么使用Profile功能
// *    1）、application.yml： 主配置文件。任何情况下都生效
// *    2）、其他Profile环境下命名规范：  application-{profile标识}.yml：
// *          比如：application-dev.yml
// *          比如：application-test.yml
// *
// *    3）、激活指定环境即可：  配置文件激活、命令行激活
// *    4）、效果：
// *        项目的所有生效配置项 = 激活环境配置文件的所有项 + 主配置文件和激活文件不冲突的所有项
// *        如果发生了配置冲突，以激活的环境配置文件为准。
// *        application-{profile标识}.yml 优先级高于 application.yml
// *
// *        主配置和激活的配置都生效，优先以激活的配置为准
// */
//
//@SpringBootApplication
//public class ApplicationStreamProfile {
//
//    public static void main(String[] args) {
////         SpringApplication.run(Application.class, args);
//
//        // 获取容器对象构造器, 流式配置属性(优先级低于配置文件)
//        ConfigurableApplicationContext context = new SpringApplicationBuilder()
//                .sources(Application.class) // 必传
//                .main(Application.class) //必传
//                .bannerMode(Banner.Mode.OFF) // 设置关闭banner
//                .run(args);
//
//        try {
//            var bean = context.getBean(PersonProfileDevTest.class);
//            System.out.println(bean);
//        }catch (Exception e){
//            System.out.println("没有找到bean-PersonProfileDevTest");
//        }
//        try {
//            var bean = context.getBean(PersonProfileDev.class);
//            System.out.println(bean);
//        }catch (Exception e){
//            System.out.println("没有找到bean-PersonProfileDev");
//        }
//        try {
//            var bean2 = context.getBean(PersonProfileTest.class);
//            System.out.println(bean2);
//        }catch (Exception e){
//            System.out.println("没有找到bean-PersonProfileTest");
//        }
//        System.exit(0);
//    }
//}
