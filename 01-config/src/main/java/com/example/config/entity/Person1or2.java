
package com.example.config.entity;

//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author booty
 */
@Component
@ConfigurationProperties(prefix = "person1") //和配置文件person前缀的所有配置进行绑定
@Data
@Profile({"config2","config3"}) // 使用该注解后, 在指定的环境下才会注入改bean, 即使配置文件中没有person1, 也会注入一个空名称对象
public class Person1or2 {
    private String name;
//    private Integer age;
//    private Date birthDay;
//    private Boolean like;
//    private Child child; //嵌套对象
//    private List<Dog> dogs; //数组（里面是对象）
//    private Map<String,Cat> cats; //表示Map
}

