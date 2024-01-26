package com.example.entity;

//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author booty
 */
@Component
@ConfigurationProperties(prefix = "person-test") //和配置文件person前缀的所有配置进行绑定
@Data
@Profile("test") // 使用该注解后, 在指定的环境下才会注入改bean
public class PersonProfileTest {
    private String name;
}

