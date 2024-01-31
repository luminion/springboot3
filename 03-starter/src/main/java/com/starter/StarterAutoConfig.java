package com.starter;

import com.starter.config.Prop;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author booty
 */
@Configuration
@Import(Prop.class) // 导入需要的组件
public class StarterAutoConfig {


    /**
     * 向容器中注入一个String类型的bean
     * 该bean的名称为username
     * 该bean的值为配置文件中booty.prop.username的值
     *
     * @param prop 道具
     * @return {@link String }
     * @author booty
     */
    @Bean
    public String username(Prop prop) {
        return prop.getUsername();
    }

}
