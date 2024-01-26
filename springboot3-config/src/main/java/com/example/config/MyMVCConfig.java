package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author booty
 */
//@EnableWebMvc // 开启全面接管SpringMVC,使用此注解后, 会禁用SpringBoot的默认配置
@Configuration
public class MyMVCConfig implements WebMvcConfigurer {



    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        /*
        默认静态资源路径(在WebMvcAutoConfiguration中定义)
        classpath:/META-INF/resources/
        classpath:/resources/
        classpath:/static/
        classpath:/public/
         */

        // 默认的规则
//        WebMvcConfigurer.super.addResourceHandlers(registry);

        // 自定义规则
        registry
                // 设置访问路径
                .addResourceHandler("/static/**")
                // 设置静态资源路径(例如,访问路径为/static/a/1.jpg,则会去classpath:/static/a/1.jpg查找)
                .addResourceLocations("classpath:/static/a/")
                .addResourceLocations("classpath:/static/b/")
                // 设置缓存时间为30分钟
                .setCacheControl(CacheControl.maxAge(1800, TimeUnit.SECONDS))
        ;

    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 系统默认会添加一些消息转换器, 例如StringHttpMessageConverter(text), MappingJackson2HttpMessageConverter(Json)等

        /*
        ● EnableWebMvcConfiguration通过 addDefaultHttpMessageConverters添加了默认的MessageConverter；如下：
          ○ ByteArrayHttpMessageConverter： 支持字节数据读写
          ○ StringHttpMessageConverter： 支持字符串读写
          ○ ResourceHttpMessageConverter：支持资源读写
          ○ ResourceRegionHttpMessageConverter: 支持分区资源写出
          ○ AllEncompassingFormHttpMessageConverter：支持表单xml/json读写
          ○ MappingJackson2HttpMessageConverter： 支持请求响应体Json读写
         */
//        WebMvcConfigurer.super.configureMessageConverters(converters);

        // 添加自定义的消息转换器
        converters.add(new YamlMessageConverter());

    }
}
