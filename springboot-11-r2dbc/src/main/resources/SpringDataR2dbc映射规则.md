### ## 基于约定的映射

`MappingR2dbcConverter`当没有提供额外的映射元数据时，有一些将对象映射到行的约定。约定是：

- 短 Java 类名按以下方式映射到表名。类`com.bigbank.SavingsAccount`映射到`SAVINGS_ACCOUNT`表名。相同的名称映射应用于将字段映射到列名称。例如，`firstName`字段映射到`FIRST_NAME`列。您可以通过提供自定义`NamingStrategy`.有关更多详细信息，请参阅[映射配置](https://docs.spring.io/spring-data/relational/reference/r2dbc/mapping.html#mapping.configuration)。默认情况下，在 SQL 语句中使用从属性或类名称派生的表名和列名时不带引号。您可以通过设置来控制此行为`RelationalMappingContext.setForceQuote(true)`。

- 不支持嵌套对象。

- 该转换器使用注册的任何 Spring 转换器`CustomConversions`来覆盖对象属性到行列和值的默认映射。

- 对象的字段用于在行中的列之间进行转换。`JavaBean`不使用公共属性。

- 如果您有一个非零参数构造函数，其构造函数参数名称与行的顶级列名称匹配，则使用该构造函数。否则，使用零参数构造函数。如果有多个非零参数构造函数，则会引发异常。有关更多详细信息，请参阅[对象创建。](https://docs.spring.io/spring-data/relational/reference/object-mapping.html#mapping.object-creation)

### 默认类型映射

下表说明了实体的属性类型如何影响映射：

| 来源类型                                        | 目标类型                           | 评论                                                                                                                             |
| ------------------------------------------- | ------------------------------ | ------------------------------------------------------------------------------------------------------------------------------ |
| 原始类型和包装类型                                   | 通道                             | 可以使用[显式转换器](https://docs.spring.io/spring-data/relational/reference/r2dbc/mapping.html#mapping.explicit.converters)进行定制。       |
| JSR-310 日期/时间类型                             | 通道                             | 可以使用[显式转换器](https://docs.spring.io/spring-data/relational/reference/r2dbc/mapping.html#mapping.explicit.converters)进行定制。       |
| `String`，`BigInteger`，`BigDecimal`， 和`UUID` | 通道                             | 可以使用[显式转换器](https://docs.spring.io/spring-data/relational/reference/r2dbc/mapping.html#mapping.explicit.converters)进行定制。       |
| `Enum`                                      | 细绳                             | 可以通过注册[显式转换器](https://docs.spring.io/spring-data/relational/reference/r2dbc/mapping.html#mapping.explicit.converters)进行定制。     |
| `Blob`和`Clob`                               | 通道                             | 可以使用[显式转换器](https://docs.spring.io/spring-data/relational/reference/r2dbc/mapping.html#mapping.explicit.converters)进行定制。       |
| `byte[]`,`ByteBuffer`                       | 通道                             | 被视为二进制有效负载。                                                                                                                    |
| `Collection<T>`                             | 数组`T`                          | [如果配置的驱动程序](https://docs.spring.io/spring-data/relational/reference/r2dbc/getting-started.html#requirements)支持，则转换为数组类型，否则不支持。 |
| 原始类型、包装类型和`String`                          | 包装类型数组（例如`int[]`→ `Integer[]`） | [如果配置的驱动程序](https://docs.spring.io/spring-data/relational/reference/r2dbc/getting-started.html#requirements)支持，则转换为数组类型，否则不支持。 |
| 驱动程序特定类型                                    | 通道                             | 由使用的 贡献为简单类型`R2dbcDialect`。                                                                                                    |
| 复杂的物体                                       | 目标类型取决于注册的`Converter`.         | 需要[显式转换器](https://docs.spring.io/spring-data/relational/reference/r2dbc/mapping.html#mapping.explicit.converters)，否则不支持。       |

### 

### 映射注释概述

可以`RelationalConverter`使用元数据来驱动对象到行的映射。可以使用以下注释：

- `@Id`：应用于字段级别，标记主键。

- `@Table`：在类级别应用以指示该类是映射到数据库的候选类。您可以指定存储数据库的表的名称。

- `@Transient`：默认情况下，所有字段都映射到该行。此注释将应用它的字段排除在数据库中的存储之外。瞬态属性不能在持久性构造函数中使用，因为转换器无法具体化构造函数参数的值。

- `@PersistenceCreator`：标记给定的构造函数或静态工厂方法（甚至是受包保护的方法）以在从数据库实例化对象时使用。构造函数参数按名称映射到检索行中的值。

- `@Value`：此注释是 Spring 框架的一部分。在映射框架内，它可以应用于构造函数参数。这使您可以使用 Spring 表达式语言语句来转换在数据库中检索到的键值，然后再将其用于构造域对象。为了引用给定行的列，必须使用如下表达式：`@Value("#root.myProperty")`其中 root 指给定的根`Row`。

- `@Column`：应用于字段级别，描述行中表示的列的名称，使该名称与类的字段名称不同。`@Column`在 SQL 语句中使用时，使用注释指定的名称始终会被引用。对于大多数数据库，这意味着这些名称区分大小写。这也意味着您可以在这些名称中使用特殊字符。但是，不建议这样做，因为它可能会导致其他工具出现问题。

- `@Version`：在字段级别应用用于乐观锁定并检查保存操作的修改。值`null`（`zero`对于原始类型）被视为新实体的标记。最初存储的值为`zero`（`one`对于原始类型）。每次更新时版本都会自动增加。请参阅[乐观锁定](https://docs.spring.io/spring-data/relational/reference/r2dbc/entity-persistence.html#r2dbc.entity-persistence.optimistic-locking)以获取更多参考。

映射元数据基础设施是在`spring-data-commons`与技术无关的单独项目中定义的。 R2DBC 支持中使用特定子类来支持基于注释的元数据。也可以采取其他策略（如果有需求）





## 命名策略

按照惯例，Spring Data 应用 `NamingStrategy`来确定表、列和模式名称，默认为[蛇形命名法](https://en.wikipedia.org/wiki/Snake_case)(下划线连接单词)。名为 的对象属性`firstName`变为`first_name`。您可以通过在应用程序上下文中提供[`NamingStrategy`](https://docs.spring.io/spring-data/jdbc/docs/3.2.5/api/org/springframework/data/relational/core/mapping/NamingStrategy.html) 来调整它



## 覆盖表名

当表命名策略与您的数据库表名不匹配时，您可以使用注释覆盖表名[`@Table`](https://docs.spring.io/spring-data/jdbc/docs/3.2.5/api/org/springframework/data/relational/core/mapping/Table.html)。此注释的元素`value`提供自定义表名称。以下示例将类映射`MyEntity`到`CUSTOM_TABLE_NAME`数据库中的表：

```java
@Table("CUSTOM_TABLE_NAME")
class MyEntity {
    @Id
    Integer id;

    String name;
}
```

## 覆盖列名称

当列命名策略与您的数据库表名不匹配时，您可以使用注释覆盖表名[`@Column`](https://docs.spring.io/spring-data/jdbc/docs/3.2.5/api/org/springframework/data/relational/core/mapping/Column.html)。此注释的元素`value`提供自定义列名称。以下示例将类`name`的属性映射`MyEntity`到`CUSTOM_COLUMN_NAME`数据库中的列：

```java
class MyEntity {
    @Id
    Integer id;

    @Column("CUSTOM_COLUMN_NAME")
    String name;
}
```

## 只读属性

用 注解的属性`@ReadOnlyProperty`不会被 Spring Data 写入数据库，但它们会在实体加载时被读取。

Spring Data 在写入实体后不会自动重新加载实体。因此，如果您想查看数据库中为此类列生成的数据，则必须显式重新加载它。

如果带注释的属性是实体或实体集合，则它由单独表中的一个或多个单独行表示。 Spring Data 不会对这些行执行任何插入、删除或更新。

## 仅插入属性

注解 的属性`@InsertOnlyProperty`只会在插入操作期间由 Spring Data 写入数据库。对于更新，这些属性将被忽略。

`@InsertOnlyProperty`仅支持聚合根。
