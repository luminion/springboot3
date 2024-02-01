package org.example.springboot02event.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户实体类
 * @author booty
 */
@Data
@Accessors(chain = true)
public class User {
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
}
