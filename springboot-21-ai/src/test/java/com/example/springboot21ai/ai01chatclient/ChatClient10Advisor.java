package com.example.springboot21ai.ai01chatclient;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ChatClient Fluent API 提供了用于配置 advisor 的 AdvisorSpec 接口。此接口提供了添加参数、一次设置多个参数以及将一个或多个 advisor 添加到链的方法
 * <p>
 * 将 advisor 添加到链中的顺序至关重要，因为它决定了它们的执行顺序。每个 advisor 都以某种方式修改 prompt 或 context，并且一个 advisor 所做的更改将传递给链中的下一个 advisor
 *
 * @author bootystar
 */
@SpringBootTest
@Slf4j
public class ChatClient10Advisor {

    @Autowired
    private ChatClient chatClient;

    @Test
    void test() {
        /*
        在执行过程中添加SimpleLoggerAdvisor用于记录日志
        需要设置
        logging.level.org.springframework.ai.chat.client.advisor=DEBUG
         */
        SimpleLoggerAdvisor customLogger = new SimpleLoggerAdvisor(
                request -> "Custom request: " + request.prompt()
                , response -> "Custom response: " + response.getResult()
                , 1
        );
        var response = chatClient.prompt()
                .advisors(customLogger)
                .user("Tell me a joke?")
                .call()
                .content();
        System.out.println("================");
        System.out.println(response);
    }


}
