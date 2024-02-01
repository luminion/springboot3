package org.example.springboot01config;

import org.example.springboot01config.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

/**
 * 启动前需要先注释掉多余的启动类
 * @author booty
 * @since 2023/12/26
 */
@SpringBootTest
@Slf4j
public class TestLog {

    @Autowired
    private Person person;

    @Test
    public void test1(){
        System.out.println(person);
    }

    @Test
    public void test2(){
        for (int i = 0; i < 1000; i++) {
            log.info("this is a log info loopCount:{}",i);
        }
    }



    @Test
    void test01(){
        System.out.println("aaaa");
    }

    @BeforeAll //所有测试方法运行之前先运行这个 ： 只打印一次
    static void initAll() {
        System.out.println("hello");
    }

    @BeforeEach//每个测试方法运行之前先运行这个 ： 每个方法运行打印一次
    void init() {
        System.out.println("world");
    }


    @ParameterizedTest //参数化测试
    @ValueSource(strings = {"one", "two", "three"}) //系统将会自动从@ValueSource中取值作为参数传入
    @DisplayName("参数化测试1")
    public void parameterizedTest1(String string) {
        System.out.println(string);
        Assertions.assertTrue(StringUtils.isNotBlank(string));
    }

    @ParameterizedTest //参数化测试
    @MethodSource("method")  //指定入参的方法名,参数来自与指定方法的返回值
    @DisplayName("😱") // 指定控制台显示的方法名称
    public void testWithExplicitLocalMethodSource(String name) {
        System.out.println(name);
        Assertions.assertNotNull(name);
    }

    //返回Stream即可
    static Stream<String> method() {
        return Stream.of("apple", "banana");
    }
}
