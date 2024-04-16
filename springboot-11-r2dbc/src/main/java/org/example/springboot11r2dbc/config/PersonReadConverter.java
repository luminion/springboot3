package org.example.springboot11r2dbc.config;

import io.r2dbc.spi.Row;
import org.example.springboot11r2dbc.entity.Person;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * 使用显式转换器覆盖映射, 手动的Person读取转化器
 *
 * @author booty
 */
@ReadingConverter
public class PersonReadConverter implements Converter<Row, Person> {

    public Person convert(Row source) {
        Person p = new Person(source.get("id", Long.class),source.get("name", String.class));
        p.setAge(source.get("age", Integer.class));
        return p;
    }
}