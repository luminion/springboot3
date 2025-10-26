package com.example.event.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户实体类
 * @author luminion
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
