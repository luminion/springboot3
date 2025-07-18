package com.example.r2dbc.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("person")
public class PersonAge {
    private Integer age;
    private List<Person> persons;


}
