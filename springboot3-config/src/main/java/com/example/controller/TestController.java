package com.example.controller;

import com.example.entity.Person;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author booty
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {


    /**
     * 测试日志打印
     *
     * @param name 姓名
     * @return {@code String }
     * @author booty
     */
    @RequestMapping("/test")
    public String test(String name){
        log.trace(name);
        log.debug(name);
        log.info(name);
        log.warn(name);
        log.error(name);
        return name;
    }


    /*
        Spring5.3 之后加入了更多的请求路径匹配的实现策略；
        以前只支持 AntPathMatcher 策略,
        现在提供了 PathPatternParser 策略。
        并且可以让我们指定到底使用那种策略

        Ant 风格的路径模式语法具有以下规则：
            ● *：表示任意数量的字符。
            ● ?：表示任意一个字符。
            ● **：表示任意数量的目录。
            ● {}：表示一个命名的模式占位符。
            ● []：表示字符集合，例如[a-z]表示小写字母。

        例如：
            ● *.html 匹配任意名称，扩展名为.html的文件。
            ● /folder1/* /*.java 匹配在folder1目录下的任意两级目录下的.java文件。
            ● /folder2/** /*.jsp 匹配在folder2目录下任意目录深度的.jsp文件。
            ● /{type}/{id}.html 匹配任意文件名为{id}.html，在任意命名的{type}目录下的文件。
        注意：Ant 风格的路径模式语法中的特殊字符需要转义，如：
            ● 要匹配文件路径中的星号，则需要转义为\\*。
            ● 要匹配文件路径中的问号，则需要转义为\\?。

        AntPathMatcher 与 PathPatternParser
            ● PathPatternParser 在 jmh 基准测试下，有 6~8 倍吞吐量提升，降低 30%~40%空间分配率
            ● PathPatternParser 兼容 AntPathMatcher语法，并支持更多类型的路径模式
            ● PathPatternParser PathPatternParser默认"**" 多段匹配的支持仅允许在模式末尾使用,若需要在路径中间使用**.需要将路径匹配策略改为ant

        # 改变路径匹配策略：
        # ant_path_matcher 老版策略；
        # path_pattern_parser 新版策略；
        spring.mvc.pathmatch.matching-strategy=ant_path_matcher

     */


    /**
     * 测试路径匹配模式
     *
     * @param request 请求
     * @param path    路径
     * @return {@code String }
     * @author booty
     */
    @GetMapping("/a*/b?/{p1:[a-f]+}")
    public String test2(HttpServletRequest request, @PathVariable("p1") String path) {

        log.info("路径变量p1： {}", path);
        //获取请求路径
        String uri = request.getRequestURI();
        return uri;
    }


    @Autowired
    private Person person;

    /**
     * 测试返回值
     * 根据指定的请求参数返回不同的数据格式
     * 默认通过Accept请求头来协商返回数据格式
     * 若通过方法参数, 需要到配置文件中开启参数协商功能
     * 若返回xml, 需要在pom中引入对应的xml依赖(默认支持, 无需配置HttpMessageConverter)
     * 若返回yaml, 需要pom中引入对应yaml依赖, 并自己配置HttpMessageConverter
     * http://localhost/test/test3?format=json
     * http://localhost/test/test3?format=xml
     * http://localhost/test/test3?format=yaml
     *
     * @return {@code Person }
     * @author booty
     */
    @GetMapping("/test3")
    public Person test3() {
        return person;
    }


    /**
     * 测试读取yaml格式参数
     * 需要pom中引入对应yaml依赖, 并自己配置HttpMessageConverter
     * 指定请求头content-type为application/yaml(与自己指定的配置相同),并在body中将yaml格式参数传入
     * yaml格式参数：没有person前缀,属性名+值即可,无缩进
     * 并返回指定类型的参数(返回格式可以与入参格式不同, 比如入参yaml, 出参json)
     *
     * @param person 人
     * @return {@code Person }
     * @author booty
     */
    @GetMapping("/test4")
    public Person test4(@RequestBody Person person) {
        /*
        yaml格式参数没有person前缀,属性名+值即可,无缩进
        例：
name: 张三
age: 18
like: true
child:
  name: 李四
  age: 20
  text: ["abc","def"]
         */
        System.out.println(person);
        return person;
    }


}
