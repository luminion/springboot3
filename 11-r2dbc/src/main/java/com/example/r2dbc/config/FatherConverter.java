package com.example.r2dbc.config;


import io.r2dbc.spi.Row;
import com.example.r2dbc.entity.Person;
import org.springframework.core.convert.converter.Converter;
import com.example.r2dbc.entity.Father;

public class FatherConverter implements Converter<Row, Father> {


    @Override
    public Father convert(Row source) {
        Father father = new Father();
        if (source.get("id") != null) {
            father.setId(source.get("id", Long.class));
        }
        if (source.get("name") != null) {
            father.setName(source.get("name", String.class));
        }
        if (source.get("age") != null) {
            father.setAge(source.get("age", Integer.class));
        }
        if (source.get("version") != null) {
            father.setVersion(source.get("version", Long.class));
        }
        Person person = new Person();
        if (source.get("c_id") != null) {
            father.setChild(source.get("c_id", Person.class));
        }
        if (source.get("c_name") != null) {
            person.setName(source.get("c_name", String.class));
        }
        if (source.get("c_age") != null) {
            person.setAge(source.get("c_age", Integer.class));
        }
        if (source.get("c_version") != null) {
            person.setVersion(source.get("c_version", Long.class));
        }
        if (source.get("c_id") != null) {
            father.setChild(person);
        }

        return father;
    }

    @Override
    public <U> Converter<Row, U> andThen(Converter<? super Father, ? extends U> after) {
        return Converter.super.andThen(after);
    }

}
