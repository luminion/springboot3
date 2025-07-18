package com.example.r2dbc;

import com.example.r2dbc.entity.Person;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 人员存储库
 * 指定实体类和主键类型
 *
 * @author booty
 */
public interface PersonRepository extends ReactiveCrudRepository<Person, Long> {


    /**
     * 该方法显示对具有给定 的所有人的查询firstname。
     * 该查询是通过解析可与And\Or连接的约束的方法名称而派生的。
     * 因此，方法名称会产生查询表达式SELECT … FROM person WHERE firstname = :firstname
     *
     * @param firstname 名字
     * @return {@link Flux }<{@link Person }>
     * @author booty
     */
    Flux<Person> findByFirstname(String firstname);

    /**
     * firstname一旦firstname给定 发出 ，该方法就会显示对所有具有给定的人的查询Publisher
     *
     * @param firstname 名字
     * @return {@link Flux }<{@link Person }>
     * @author booty
     */
    Flux<Person> findByFirstname(Publisher<String> firstname);

    /**
     * 用于Pageable将偏移和排序参数传递到数据库
     *
     * @param firstname 名字
     * @param pageable  可分页
     * @return {@link Flux }<{@link Person }>
     * @author booty
     */
    Flux<Person> findByFirstnameOrderByLastname(String firstname, Pageable pageable);

    /**
     * 查找符合给定条件的单个实体。
     * 若检索出多条数据, 会抛出IncorrectResultSizeDataAccessException非唯一的结果
     *
     * @param firstname 名字
     * @param lastname  姓氏
     * @return {@link Mono }<{@link Person }>
     * @author booty
     */
    Mono<Person> findByFirstnameAndLastname(String firstname, String lastname);

    /**
     * 	除非只查询出一条结果, 否则即使查询生成更多结果行，也始终会返回第一个实体
     *
     * @param lastname 姓氏
     * @return {@link Mono }<{@link Person }>
     * @author booty
     */
    Mono<Person> findFirstByLastname(String lastname);

    /**
     * 通过具名表达式查询 :lastname表示参数名为lastname
     * 请注意，注释中使用的 select 语句的列必须与为相应属性@Query生成的名称相匹配。
     * 如果 select语句不包含匹配列，则不会设置该属性。
     * 如果持久性构造函数需要该属性，则提供 null 或（对于基本类型）默认值
     * 注意:
     * 请注意，基于字符串的查询不支持分页，也不接受Sort, PageRequest,Limit作为查询参数，对于这些查询，需要重写查询。
     * 如果您想应用限制，请使用 SQL 表达此意图并自行将适当的参数绑定到查询
     *
     * @param lastname 姓氏
     * @return {@link Flux }<{@link Person }>
     * @author booty
     */
    @Query("SELECT * FROM person WHERE lastname = :lastname")
    Flux<Person> findByLastname(String lastname);

    /**
     * 通过占位序号的方式查询, $1代表第一个参数
     * 请注意，注释中使用的 select 语句的列必须与为相应属性@Query生成的名称相匹配。
     * 如果 select语句不包含匹配列，则不会设置该属性。
     * 如果持久性构造函数需要该属性，则提供 null 或（对于基本类型）默认值
     * 注意:
     * 请注意，基于字符串的查询不支持分页，也不接受Sort, PageRequest,Limit作为查询参数，对于这些查询，需要重写查询。
     * 如果您想应用限制，请使用 SQL 表达此意图并自行将适当的参数绑定到查询
     *
     * @param lastname 姓氏
     * @return {@link Mono }<{@link Person }>
     * @author booty
     */
    @Query("SELECT firstname, lastname FROM person WHERE lastname = $1")
    Mono<Person> findFirstByLastname2(String lastname);


    /**
     * 使用 SpEL 表达式进行查询
     * 查询字符串定义可以与 SpEL 表达式一起使用，以在运行时创建动态查询。 SpEL 表达式可以提供在运行查询之前计算的谓词值。
     * 表达式通过包含所有参数的数组公开方法参数。以下查询用于[0] 声明谓词值lastname（相当于:lastname参数绑定）：
     * @param lastname 姓氏
     * @return {@link Flux }<{@link Person }>
     * @author booty
     */
    @Query("SELECT * FROM person WHERE lastname = :#{[0]}")
    Flux<Person> findByQueryWithExpression(String lastname);

}
