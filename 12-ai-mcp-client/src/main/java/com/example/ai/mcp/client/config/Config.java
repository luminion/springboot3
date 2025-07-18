package com.example.ai.mcp.client.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 引入mcp客户端之后, 
 * 可以通过toolCallbackProvider获取mcp服务的工具
 * @author bootystar
 */
@Configuration(proxyBeanMethods = false)
public class Config {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider toolCallbackProvider) {
        return builder
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultSystem("""
                        在条件允许的情况下, 尽量以中文交流
                        并告诉用户你的工作流程,调用了哪些外部工具
                        """)
                .build()
                ;
    }
}
