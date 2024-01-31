package com.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author booty
 */
@ConfigurationProperties(prefix = "booty.prop")  //此属性类和配置文件指定前缀绑定
@Component
@Data
public class Prop {
    private String username;
    private String pwd;
    private String age;
    private String email;
}
