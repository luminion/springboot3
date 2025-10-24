package com.example.config.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author luxmixus
 */
@Data
public class Child {
    private String name;
    private Integer age;
    private Date birthDay;
    private List<String> text; //数组
}