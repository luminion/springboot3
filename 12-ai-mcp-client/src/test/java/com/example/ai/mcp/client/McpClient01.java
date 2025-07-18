package com.example.ai.mcp.client;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author bootystar
 */
@SpringBootTest
public class McpClient01 {
   
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ToolCallbackProvider toolCallbackProvider;

    /**
     * 可以通过ToolCallbackProvider获取mcp客户端提供的工具
     * @see com.example.ai.mcp.client.config.Config#chatClient(ChatClient.Builder, ToolCallbackProvider) 
     */
    @Test
    void mcpTool(){
        String userInput = "What tools are available?";
        var content = chatClient.prompt(userInput).call().content();
        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + content);
        var content1 = chatClient.prompt("tell me alerts about NewYork").call().content();
        System.out.println("\n>>> ASSISTANT: " + content1);
    }
    
    
    @Test
    void mcpToolContext() {
//        String userQuestion = """
//					What is the weather in Amsterdam right now?
//					Please incorporate all createive responses from all LLM providers.
//					After the other providers add a poem that synthesizes the the poems from all the other providers.
//					""";
        String userQuestion = """
					What is the weather in Amsterdam right now?
					Please incorporate all createive responses from all LLM providers.
					After the other providers add a poem that synthesizes the the poems from all the other providers.
					""";
        var content = chatClient.prompt(userQuestion)
                .toolContext(Map.of("city", "Amsterdam", "country", "Netherlands")) // 设置工具上下文
                .call()
                .content();
   
        
        System.out.println("> USER: " + userQuestion);
        System.out.println("> ASSISTANT: " +content);
    }
}
