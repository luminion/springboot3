package com.example.webflux.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;


/**
 * WebFluxConfigurer相当于MVC中的WebMvcConfigurer
 * 用于配置网络请求的基础支持,如跨域策略等
 *
 * @author booty
 */
@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //配置跨域策略
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowedOrigins("localhost");
    }



}
