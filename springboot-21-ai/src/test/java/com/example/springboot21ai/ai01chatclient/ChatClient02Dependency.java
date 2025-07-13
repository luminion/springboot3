package com.example.springboot21ai.ai01chatclient;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Scanner;

/**
 * 根据引入的依赖不同, 使用不同平台的AI模型, 选择使用哪个
 * 需要使用不同的ChatModel提前手动创建bean
 * 配置文件参考
 * application-openai.yml
 * application-deepseek.yml
 * 配置类参考
 * {@link com.example.springboot21ai.config.ChatClientConfig#openAiChatClient}
 * {@link com.example.springboot21ai.config.ChatClientConfig#deepSeekChatClient}
 * 实际应用中, 一般的大模型都兼容openai, 所以一般使用openAi即可
 * @author bootystar
 */
@SpringBootTest
@Slf4j
class ChatClient02Dependency {

    @Autowired
    @Qualifier("openAiChatModel")
    private OpenAiChatModel openAiChatModel;
    @Autowired
    @Qualifier("deepSeekChatModel")
    private DeepSeekChatModel deepSeekChatModel;

    /**
     * 多平台聊天客户端
     * test不能使用scanner输入
     * 实际使用时需要主方法运行或在spring中注入CommandLineRunner运行:
     * {@link com.example.springboot21ai.config.ChatClientConfig#runMultipleModel(ChatClient, ChatClient)} 
     */
    @Test
    @SneakyThrows
    void multiPlatformChatClient() {
        // test不能使用控制台输入, 仅作为代码演示, 实际使用时需要主方法运行
        // 多个不同平台的AI模型, 选择使用哪个
        var scanner = new Scanner(System.in);
        ChatClient chat;

        // Model selection
        System.out.println("\nSelect your AI model:");
        System.out.println("1. OpenAI");
        System.out.println("2. DeepSeek");
        System.out.print("Enter your choice (1 or 2): ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            chat = ChatClient.create(openAiChatModel);
            System.out.println("Using OpenAI model");
        } else {
            chat = ChatClient.create(deepSeekChatModel);
            System.out.println("Using DeepSeek model");
        }
        while (true) {
            System.out.print("\nEnter your question: (press 'q' to exit)");
            String input = scanner.nextLine();
            if (input.equals("q")){
                break;
            }
            // Use the selected chat client
            String response = chat.prompt(input).call().content();
            System.out.println("ASSISTANT:\n" + response);
        }
        scanner.close();
    }
    
    
   
    

}
