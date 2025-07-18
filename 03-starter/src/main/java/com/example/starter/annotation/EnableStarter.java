package com.example.starter.annotation;

import com.example.starter.StarterAutoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用starter的注解
 * 在启动类上添加该注解即可启用该starter
 * 若在METE-INF/spring下的org.springframework.boot.autoconfigure.AutoConfiguration.imports文件中指定了Config,
 * 则无需添加该注解也能自动生效
 *
 * 注意:
 * 在springboot3版本低于3时,对应配置文件为METE-INF/spring.factories,
 * 并在配置文件中指定org.springframework.boot.autoconfigure.EnableAutoConfiguration=xxx
 *
 * @author bootystar
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(StarterAutoConfig.class) // 导入需要的组件
public @interface EnableStarter {


}
