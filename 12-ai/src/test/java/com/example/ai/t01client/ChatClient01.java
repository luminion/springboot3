package com.example.ai.t01client;

import com.example.ai.config.ChatClientConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * ChatModel 类类似于 JDK 中的核心 JDBC 库。
 * ChatClient 类可以比作 JdbcClient，它构建在 ChatModel 之上，并通过 Advisor 提供更高级的构造 
 * 要考虑过去与模型的交互，请使用额外的上下文文档来增强提示，并引入代理行为
 * 引入对应依赖后, 在配置文件中添加配置后即可直接使用ChatModel
 * 如果需要使用进一步的封装, 则需要使用ChatClient, ChatClient需要自己通过ChatModel创建
 * {@link ChatClientConfig#chatClient}
 *
 * @author bootystar
 */
@SpringBootTest
class ChatClient01 {

    @Autowired
    private ChatClient chatClient;

    @Test
    void chat(){
        var content = chatClient
                .prompt()
                .system("全程用中文回答")
                .user("给我讲个笑话")
                .call()// 同步阻塞请求
                .content() // 获取内容
                ;
        System.out.println(content);
    }
    
    @Test
    @SneakyThrows
    void fluxChat() {
        // 使用stream()方法获得异步响应的流
        Flux<String> flux = chatClient
                .prompt()
                .system("全程用中文回答")
                .user("你是哪个平台的哪个模型")
                .stream()// 异步响应
                .content()// 获取内容
                ;
        flux.delayElements(Duration.ofMillis(100)) // 延迟100毫秒, 用于观察异步效果
                .subscribe(System.out::print)
                ;
        TimeUnit.SECONDS.sleep(30);
        System.out.println();
    }
    

}
