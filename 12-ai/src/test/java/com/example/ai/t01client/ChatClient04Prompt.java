package com.example.ai.t01client;

import com.example.ai.t02prompt.Prompt01TemplateEngine;
import com.example.ai.t02prompt.Prompt02StructuredOutput;
import com.example.ai.entity.ActorsFilms;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Fluent API 允许您使用重载的提示方法以三种不同的方式创建提示
 * 1. prompt()
 * 这种没有参数的方法可以让你开始使用 Fluent API，从而允许你构建 user、system 和 prompt 的其他部分
 * 2. prompt(Prompt prompt)
 * 此方法接受 Prompt 参数，允许您传入使用 Prompt 的非 Fluent API 创建的 Prompt 实例
 * 3. prompt(String content)
 * 这是一种类似于前面的重载的便捷方法。它获取用户的文本内容,也就是user的content
 * <p>
 * <p>
 * 在 ChatClient 上指定 call() 方法后，响应类型有几种不同的选项
 * <p>
 * 1. String content()：
 * 返回响应的 String 内容
 * 2. ChatResponse chatResponse()：
 * 返回包含多个代以及有关响应的元数据的 ChatResponse 对象，例如用于创建响应的令牌数。
 * 3. ChatClientResponse chatClientResponse() ：
 * 返回一个 ChatClientResponse 对象，其中包含 ChatResponse 对象和 ChatClient 执行上下文，允许您访问在执行顾问程序期间使用的其他数据（例如，在 RAG 流中检索的相关文档）
 * 4. entity()：
 * 返回 Java 类型
 * 5. entity(ParameterizedTypeReference<T> type) ：
 * 用于返回实体类型的 Collection
 * 6. entity（Class<T> type）：
 * 用于返回特定的实体类型
 * 7. entity(StructuredOutputConverter<T> structuredOutputConverter) ：
 * 用于指定 StructuredOutputConverter 的实例，以将 String 转换为实体类型
 * <p>
 * <p>
 * 与 call() 方法类似，响应式的stream()方法也允许您访问响应的不同类型
 * <p>
 * 1. Flux<String> content()：
 * 返回 AI 模型生成的字符串的 Flux。
 * 2. Flux<ChatResponse> chatResponse()：
 * 返回包含多个代以及有关响应的元数据的 ChatResponse 对象的 Flux，例如用于创建响应的令牌数。
 * 3. Flux<ChatClientResponse> chatClientResponse()：
 * 返回一个 ChatClientResponse 对象的 Flux, 其中包含 ChatResponse 对象和 ChatClient 执行上下文，使您能够访问在执行顾问程序期间使用的其他数据（例如，在 RAG 流中检索的相关文档）
 * 流失输出返回需要结合BeanOutputConverter使用, 详见{@link Prompt02StructuredOutput}
 *
 * @author bootystar
 */
@SpringBootTest
public class ChatClient04Prompt {

    @Autowired
    private ChatClient chatClient;

    /**
     * 获取响应的元对象信息
     */
    @Test
    void promptChatResponse() {
        var tellMeAJoke = chatClient
                .prompt()
                .user("Tell me a joke")
                .call()
                .chatResponse() // 包含元数据的 ChatResponse 对象
                ;
        System.out.println(tellMeAJoke);
    }


    /**
     * 响应返回实体类
     *
     * @see Prompt02StructuredOutput 自定义封装响应结果
     */
    @Test
    void promptEntity() {
        var resp = chatClient
                .prompt()
                .user("""
                        Generate the filmography for a random actor.
                        return with json
                        actor:
                        name  str
                        age   int
                        List<Movie> movies
                        Movie:
                        name  string
                        year  int
                        """)
                .call()
                .entity(ActorsFilms.class) // 转化为实体类
                ;
        System.out.println(resp);
    }


    /**
     * 响应返回带泛型的实体类
     *
     * @see Prompt02StructuredOutput 自定义封装响应结果
     */
    @Test
    void promptParameterizedTypeReference() {
        var resp = chatClient
                .prompt()
                .user("""
                        Generate the filmography for 3 random actor.
                        return with json
                        actor:
                        name  str
                        age   int
                        List<Movie> movies
                        Movie:
                        name  string
                        year  int
                        """)
                .call()
                .entity(new ParameterizedTypeReference<List<ActorsFilms>>() {
                }) // 带泛型的实体类
                ;
        assert resp != null;
        resp.forEach(System.out::println);
    }


    /**
     * 响应式调用返回实体类
     *
     * @see Prompt02StructuredOutput 自定义封装响应结果
     */
    @Test
    void promptBeanOutputConverter() {
        // 格式转化器
        var converter = new BeanOutputConverter<>(new ParameterizedTypeReference<List<ActorsFilms>>() {
        });
        Flux<String> flux = chatClient
                .prompt()
                .user(u -> u.text("""
                                  Generate the filmography for 4 random actor.
                                  {format}
                                """) // 指定模板, 使用{}占位
                        .param("format", converter.getFormat())) // 指定按给定的格式转化器输出
                .stream()
                .content();
        String content = String.join("", flux.collectList().block());
        List<ActorsFilms> actorFilms = converter.convert(content);
        actorFilms.forEach(System.out::println);
    }


    /**
     * 使用模板占位符
     *
     * @see Prompt01TemplateEngine 自定义模板
     */
    @Test
    void promptTemplate() {
        String answer = chatClient.prompt()
                .user(u -> u
                        .text("Tell me the names of 5 movies whose soundtrack was composed by {composer}")
                        .param("composer", "John Williams"))
                .call()
                .content();
        System.out.println(answer);
    }


}
