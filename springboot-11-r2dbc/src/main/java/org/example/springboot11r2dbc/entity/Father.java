package org.example.springboot11r2dbc.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.InsertOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("person") // 指定表名
public class Father {

    @Id // 指定主键id
    private Long id;
    @Column("name") // 指定列名
    private String name;
    @InsertOnlyProperty // 只插入数据, 对于更新，这些属性将被忽略
    private int age;
    @Version // 乐观锁
    private Long version;
    @ReadOnlyProperty // 只读属性, 该属性不会写入数据库
//    @Transient //表示该字段在数据库中不存在
    private Person child;


    public Father(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Father(String name) {
        this.name = name;
    }
}