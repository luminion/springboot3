package com.example;

import com.example.entity.Person;
import com.example.entity.Person1or2;
import com.example.entity.Person2;
import com.example.entity.Person3;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.stream.Stream;

/**
 *
 * 环境隔离：
 * 任何@Component, @Configuration, @Bean,  @ConfigurationProperties 标记的类
 * 可以使用 @Profile 标记，来指定何时被加载。【容器中的组件都可以被 @Profile标记】
 *
 * 1、标识环境
 *    1）、区分出几个环境： dev（开发环境）、test（测试环境）、prod（生产环境）
 *    2）、指定每个组件在哪个环境下生效； default环境：默认环境
 *          通过： @Profile({"test"})标注
 *          组件没有标注@Profile代表任意时候都生效
 *    3）、默认只有激活指定的环境，这些组件才会生效。
 * 2、激活环境
 *    配置文件激活：spring.profiles.active=dev；
 *    命令行激活： java -jar xxx.jar  --spring.profiles.active=dev
 *
 * 3、配置文件怎么使用Profile功能
 *    1）、application.yml： 主配置文件。任何情况下都生效
 *    2）、其他Profile环境下命名规范：  application-{profile标识}.yml：
 *          比如：application-dev.yml
 *          比如：application-test.yml
 *
 *    3）、激活指定环境即可：  配置文件激活、命令行激活
 *    4）、效果：
 *        项目的所有生效配置项 = 激活环境配置文件的所有项 + 主配置文件和激活文件不冲突的所有项
 *        如果发生了配置冲突，以激活的环境配置文件为准。
 *        application-{profile标识}.yml 优先级高于 application.yml
 *
 *        主配置和激活的配置都生效，优先以激活的配置为准
 *
 * @author booty
 * @since 2023/12/26
 */
@SpringBootTest
@Slf4j
public class TestConfigEnvironment {
    @Autowired
    private ApplicationContext context;

    @Test
    void test1(){
        // 通过修改配置文件中spring.profile.active，来激活不同的环境
        try {
            var bean = context.getBean(Person1or2.class);
            System.out.println(bean);
        }catch (Exception e){
            System.out.println("没有找到bean-Person1or2");
        }
        try {
            var bean = context.getBean(Person2.class);
            System.out.println(bean);
        }catch (Exception e){
            System.out.println("没有找到bean-Person2");
        }
        try {
            var bean2 = context.getBean(Person3.class);
            System.out.println(bean2);
        }catch (Exception e){
            System.out.println("没有找到bean-Person3");
        }
    }



}
