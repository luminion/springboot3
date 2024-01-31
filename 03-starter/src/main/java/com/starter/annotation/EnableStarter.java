package com.starter.annotation;

import com.starter.StarterAutoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用starter的注解
 * 在启动类上添加该注解即可启用该starter
 * 若在METE-INF/spring下的org.springframework.boot.autoconfigure.AutoConfiguration.imports文件中指定了Config,
 * 则无需添加该注解也能自动生效
 * @author booty
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(StarterAutoConfig.class) // 导入需要的组件
public @interface EnableStarter {


}
