package com.example.springboot21ai.ai10toolcalling;

import com.example.springboot21ai.tool.CustomerTools;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

/**
 * Spring AI 支持通过 ToolContext API 将额外的上下文信息传递给工具。
 * 此功能允许您提供额外的用户提供的数据，这些数据可与 AI 模型传递的工具参数一起在工具执行中使用
 * ToolContext 中提供的任何数据都不会发送到 AI 模型
 * @author bootystar
 */
@SpringBootTest
public class ToolCall06ToolContext {
    @Autowired
    @Qualifier("qwen_plus")
    private ChatModel chatModel;

    /**
     * 从工具上下文中获取信息
     * {@link CustomerTools#getCustomerInfo(Long, ToolContext)}
     */
    @Test
    void getInfoFromToolContext() {
        String response = ChatClient.create(chatModel)
                .prompt("Tell me more about the special customer with ID 42")
                .tools(new CustomerTools())
                .toolContext(Map.of("name", "jack","email","jack@gmail.com"))
                .call()
                .content();

        System.out.println(response);   
    }


    /**
     * 设置默认的工具上下文
     */
    @Test
    void defaultToolContext(){
        var chatClient = ChatClient.builder(chatModel)
                .defaultToolContext(Map.of("name", "jack","email","jack@gmail.com"))
                .build();
        String response = chatClient
                .prompt("Tell me more about the special customer with ID 42")
                .tools(new CustomerTools())
                .call()
                .content();
        System.out.println(response);
    }
    
}
