package com.example.r2dbc;

import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import com.example.r2dbc.entity.Person;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.domain.Sort.Order.desc;
import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;
import static org.springframework.data.relational.core.query.Update.update;


/**
 * 原生r2dbc写法
 *
 * @author booty
 */
@Slf4j
public class T01R2dbcWithOutSpringTest {
    static R2dbcEntityTemplate template;
    static MySqlConnectionFactory connectionFactory;
    @BeforeAll
    static void init(){
        MySqlConnectionConfiguration connectionConfiguration = MySqlConnectionConfiguration.builder()
                .host("localhost")
                .username("root")
                .password("root")
                .port(3306)
                .database("r2dbc")
                .serverZoneId(ZoneId.systemDefault())
                .build();
        MySqlConnectionFactory mySqlConnectionFactory = MySqlConnectionFactory.from(connectionConfiguration);
        connectionFactory=mySqlConnectionFactory;
        template = new R2dbcEntityTemplate(mySqlConnectionFactory);
    }



    /**
     * 将数据库查询结果转换为实体
     * 1.创建链接
     * 2.创建Statement
     * 3.执行sql
     * 4.将结果转换为实体
     * 该过程和jdbc的基础操作基本类似
     *
     * @author booty
     */
    @Test
    void queryByConnection() throws Exception{
        // JDBC： Statement： 封装sql的
        //3、数据发布者
        Mono.from(connectionFactory.create())
                .flatMapMany(connection ->
                        connection
                                // id=?id,代表参数名为id, 也可以使用下标方式标记
                                .createStatement("select * from person where id=?id or name=?name")
                                .bind("id",1L) //具名参数
                                .bind("name","zhangsan")
                                .execute()
                ).flatMap(result -> {
                    return result.map(readable -> {
                        Long id = readable.get("id", Long.class);
                        String name = readable.get("name", String.class);
                        return new Person(id, name);
                    });
                })
                .subscribe(person -> System.out.println("person = " + person));
        TimeUnit.SECONDS.sleep(5);
    }




    /**
     * R2dbcEntityTemplate是 Spring Data R2DBC 的中心入口点。
     * 它为典型的临时用例（例如查询、插入、更新和删除数据）提供直接的面向实体的方法和更窄、更流畅的接口
     * 入口点（insert()、select()、update()等）遵循基于要运行的操作的自然命名模式。
     * 从入口点开始，该 API 旨在仅提供上下文相关的方法，这些方法会导致创建并运行 SQL 语句的终止方法。
     * Spring Data R2DBC 使用R2dbcDialect抽象来确定绑定标记、分页支持以及底层驱动程序本机支持的数据类型
     * 所有终端方法始终返回Publisher表示所需操作的类型。实际的报表在订阅后发送到数据库
     *
     * @author booty
     */
    @Test
    void r2dbcEntityTemplate() {
//        ConnectionFactory connectionFactory = ConnectionFactories.get("r2dbc:h2:mem:///test?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        template.getDatabaseClient()
                .sql("CREATE TABLE person" +
                        "(id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                        "name VARCHAR(255)," +
                        "age INT)")
                .fetch() // 执行SQL调用并通过进入执行阶段检索结果
                .rowsUpdated()// 获取影响的行数
                .as(StepVerifier::create)// 转换为StepVerifier
                .expectNextCount(1) // 断言影响的行数为1
                .verifyComplete() // 断言完成,否则抛出异常
                ;

        template.insert(Person.class)
                .using(new Person(1L, "Joe"))// 只插入一条数据
                .as(StepVerifier::create)// 转换为StepVerifier
                .expectNextCount(1)// 断言影响的行数为1
                .verifyComplete()// 断言完成,否则抛出异常
        ;

        template.select(Person.class)
                .first() // 获取第一个结果或没有结果,若未发现则返回Mono.empty()
                .doOnNext(it -> log.info("entity:{}",it)) // 打印实体类
                .as(StepVerifier::create)// 转换为StepVerifier
                .expectNextCount(1) // 断言影响的行数为1
                .verifyComplete()// 断言完成,否则抛出异常
        ;
    }


    /**
     * select(…)和方法selectOne(…)用于R2dbcEntityTemplate从表中选择数据。
     * 两种方法都采用一个Query定义字段投影、WHERE子句、ORDER BY子句和限制/偏移分页的对象。
     * 无论底层数据库如何，限制/偏移功能对应用程序都是透明的。该功能由R2dbcDialect抽象支持，以满足各个 SQL 风格之间的差异
     */
    @Test
    void queryApi() throws Exception{

        // 简单查询,查询全部
        System.out.println("====查询全部====");
        Flux<Person> people = template.select(Person.class).all()
        ;
        people.subscribe(person -> System.out.println("person = " + person))
        ;
        TimeUnit.SECONDS.sleep(3);

        /*
        更复杂的查询，该查询通过名称、条件WHERE和ORDER BY子句指定表名
        可以通过提供目标类型来直接将投影select(Class<?>)应用于结果, 指定返回的类型
        您可以通过以下终止方法在检索单个实体和检索多个实体之间切换：

        first()：仅使用第一行，返回Mono.如果查询没有返回结果，则返回Mono完成而不发出对象。
        one()：仅消耗一行，返回Mono.如果查询没有返回结果，则返回Mono完成而不发出对象。如果查询返回多于一行，则Mono会抛出IncorrectResultSizeDataAccessException。
        all()：消耗所有返回的行返回Flux.
        count()：应用计数投影返回Mono<Long>。
        exists()：通过返回 来返回查询是否产生任何行Mono<Boolean>。

        您可以使用select()入口点来表达您的SELECT查询。
        生成的SELECT查询支持常用子句 (WHERE和ORDER BY) 并支持分页。
        流畅的 API 风格让您可以将多个方法链接在一起，同时拥有易于理解的代码。
        为了提高可读性，您可以使用静态导入，这样您就可以避免使用“new”关键字来创建Criteria实例。
         */

        System.out.println("====复杂查询====");
        Mono<Person> first = template.select(Person.class)
                // 按名称从表中选择将返回使用给定域类型的行结果
                .from("person")

                .matching(
                        query(
                                // 发出的查询声明一个WHERE条件 按id和name列来过滤结果
                                where("id").is(1L).and("name").in("zhangsan", "lisi")
                        )
                        // 结果可以按各个列名称排序，从而形成一个ORDER BY子句
                        .sort(by(desc("id"))
                        )
                )
                // 选择一个结果仅获取一行。这种使用行的方式期望查询准确地返回单个结果。
                // 如果查询产生多个结果，则抛出异常IncorrectResultSizeDataAccessException
                .one();
        first.subscribe(person -> System.out.println("person = " + person));
        TimeUnit.SECONDS.sleep(3);
    }




    /**
     * 按条件查询
     * Criteria是用于创建查询的中心类。它遵循流畅的API风格，因此您可以轻松地将多个条件链接在一起
     * 该类Criteria提供了以下方法，所有这些方法都对应于SQL操作符：
     * Criteria and ：将指定的(String column)链接添加到当前链接并返回新创建的链接。Criteria propertyCriteria
     * Criteria or ：将指定的(String column)链接添加到当前链接并返回新创建的链接。Criteria propertyCriteria
     * Criteria GreaterThan (Object o)：使用>运算符创建条件。
     * Criteria GreaterThanOrEquals (Object o)：使用>=运算符创建条件。
     * Criteria in ：通过使用varargs 参数的运算符(Object…​ o)创建条件。IN
     * Criteria in ：通过使用使用集合的运算(Collection<?> collection)符来创建条件。IN
     * Criteria is (Object o)：使用列匹配 ( ) 创建条件property = value。
     * Criteria isNull ()：使用IS NULL运算符创建条件。
     * Criteria isNotNull ()：使用IS NOT NULL运算符创建条件。
     * Criteria lessThan (Object o)：使用<运算符创建条件。
     * Criteria lessThanOrEquals (Object o)：使用⇐运算符创建条件。
     * Criteria like (Object o)：使用LIKE不进行转义字符处理的运算符创建条件。
     * Criteria not (Object o)：使用运算符创建条件!=。
     * Criteria notIn ：通过使用varargs 参数的运算(Object…​ o)符创建条件。NOT IN
     * Criteria notIn ：通过使用使用集合的运算(Collection<?> collection)符来创建条件。NOT IN
     * 您可以使用Criteria with SELECT、UPDATE、 和DELETE查询。
     *
     *
     * @author booty
     */
    @Test
    void queryByCriteria() throws Exception {

        // Query By Criteria: QBC
        //使用Criteria构造查询条件  where id=1 and name=zhangsan
        Criteria criteria = Criteria
                .empty() // 创建一个空的Criteria
                .and("id").is(1L)
                .or("name").is("zhangsan");

        //2、封装为 Query 对象
        Query query = query(criteria);
        template
                .select(query, Person.class)
                .subscribe(person -> System.out.println("person = " + person));
        TimeUnit.SECONDS.sleep(3);
    }


    /**
     * 插入数据
     *
     * @author booty
     */
    @Test
    void insertTest() throws Exception{
        // 简单的插入
        System.out.println("====简单插入====");
        Mono<Person> insert = template
                // 使用Person该方法根据映射元数据into(…)设置表。INTO它还准备插入语句以接受Person要插入的对象
                .insert(Person.class)
                // 	提供一个标量Person对象。或者，您可以提供 一个Publisher来运行语句流INSERT。此方法提取所有非null值并插入它们
                .using(new Person(5L, "Doe"));
        insert.subscribe(person -> System.out.println("person = " + person));
        TimeUnit.SECONDS.sleep(3);
    }


    /**
     * 更新数据
     *
     * @author booty
     */
    @Test
    void updateTest() throws Exception{
        System.out.println("====更新====");
        Mono<Long> update = template
                // 更新Person对象并根据映射元数据应用映射
                .update(Person.class)
                // 通过调用该方法设置不同的表名inTable(…)
                .inTable("person")
                // 指定转换为子句的查询WHERE
                .matching(query(where("name").is("zhangsan")))
                // 应用Update对象。在本例中设置age为42并返回受影响的行数
                .apply(update("age", 42))
        ;
        update.subscribe(rows -> System.out.println("受影响的行数 = " + rows));
        TimeUnit.SECONDS.sleep(3);
    }

    /**
     * 删除
     *
     * @author booty
     */
    @Test
    void deleteTest() throws Exception{
        System.out.println("====删除====");
        Mono<Long> delete = template
                // 删除Person对象并根据映射元数据应用映射
                .delete(Person.class)
                // 通过调用该方法设置不同的表名
                .from("person")
                // 指定转换为子句的查询WHERE
                .matching(query(where("name").is("lis")))
                // 应用删除操作并返回受影响的行数
                .all();
        delete.subscribe(rows -> System.out.println("受影响的行数 = " + rows));
        TimeUnit.SECONDS.sleep(3);
    }












}
