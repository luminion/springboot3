package org.example.springboot01config.entity;

//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author booty
 */
@Component
@ConfigurationProperties(prefix = "person") //和配置文件person前缀的所有配置进行绑定
@Data
@Profile("config3") // 使用该注解后, 在指定的环境下才会注入改bean
public class Person3 {
    private String name;
}

