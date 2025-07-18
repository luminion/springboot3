package com.example.r2dbc.config;

import com.example.r2dbc.entity.Person;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;

/**
 * 使用显式转换器覆盖映射, 手动的Person写入转化器
 *
 * @author booty
 */
@WritingConverter
public class PersonWriteConverter implements Converter<Person, OutboundRow> {

    public OutboundRow convert(Person source) {
        OutboundRow row = new OutboundRow();
        row.put("id", Parameter.from(source.getId()));
        row.put("name", Parameter.from(source.getName()));
        row.put("age", Parameter.from(source.getAge()));
        return row;
    }
}