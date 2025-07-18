package com.example.r2dbc.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Person {

    @Id // 指定主键id
    private Long id;
    private String name;
    private String firstname;
    private String lastname;
    private int age;
    @Version // 乐观锁
    private Long version;


    public Person(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Person(String name) {
        this.name = name;
    }
}