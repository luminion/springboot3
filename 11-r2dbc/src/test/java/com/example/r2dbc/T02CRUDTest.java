package com.example.r2dbc;


import com.example.r2dbc.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

/**
 * 使用spring 注入R2dbcEntityTemplate
 * 并测试CRUD
 * @author booty
 */
@SpringBootTest
public class T02CRUDTest {

    @Autowired
    R2dbcEntityTemplate template;


    /**
     *
     * @Id
     * Spring Data 使用 identifer 属性来标识实体。实体的ID必须用Spring Data的@Id注解来注解。
     * 当数据库的 ID 列有自动增量列时，生成的值将在将其插入数据库后在实体中设置。
     * 当实体是新的且标识符值默认为其初始值时，Spring Data 不会尝试插入标识符列的值。这0适用于基元类型，并且null标识符属性使用数字包装类型,例如Long
     * 实体状态检测详细解释了检测实体是否是新实体或是否应该存在于数据库中的策略。
     * 一个重要的限制是，保存实体后，该实体不能再是新的。请注意，实体是否是新的是实体状态的一部分。
     * 对于自动增量列，这种情况会自动发生，因为 ID 是由 Spring Data 使用 ID 列中的值设置的。
     *
     * @author booty
     */
    @Test
    void idPolicy() throws Exception{
        Person person = new Person();
//        person.setId(7L);
        person.setName("wangba");
        template.insert(person).subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(3);
    }


    /**
     * @Version
     * Spring Data 通过在聚合根上注释的数字属性来支持乐观锁定 。
     * 每当 Spring Data 保存具有此类版本属性的聚合时，都会发生两件事：
     * 聚合根的更新语句将包含一个 where 子句，检查数据库中存储的版本实际上是否未更改。
     * 如果不是这种情况，OptimisticLockingFailureException则会被抛出。
     * 此外，实体和数据库中的版本属性都会增加，因此并发操作将注意到更改并抛出异常（OptimisticLockingFailureException如果适用），如上所述。
     * 此过程也适用于插入新聚合，其中null或0版本表示新实例，随后增加的实例将实例标记为不再是新的，
     * 使得此过程在对象构造期间生成 id 的情况下工作得相当好，例如当 UUID 是用过的。
     * 在删除期间，版本检查也适用，但不会增加版本。
     */
    @Test
    void versionPolicy() throws Exception{
        Mono<Person> daenerys = template.insert(new Person("Daenerys"));
        Person first = daenerys.block();

        Person second = template.select(Person.class)
                .matching(query(where("id").is(first.getId())))
                .first()
                .block();
        System.out.println("beforeUpdate second: "+ second);

        // 第一次更新, 更新后版本会变化+1
        first.setName("Targaryen");
        template.update(first).subscribe(e->System.out.println("afterUpdate first: "+ e));
        ;

        // 抛出OptimisticLockingFailureException, 因为second.version = 0;不为1
        second.setName("Lucy");
        template.update(second).subscribe(e->System.out.println("afterUpdate second: "+ e));

        TimeUnit.SECONDS.sleep(3);
    }





}
