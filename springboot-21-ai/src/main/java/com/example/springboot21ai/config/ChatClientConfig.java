package com.example.springboot21ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

/**
 * ChatClient是对ChatModel的高级抽象，提供更高级的封装和更便捷的API
 * 系统默认没有配置ChatClient
 * 需要自己配置bean
 *
 * @author bootystar
 */
@Configuration
@Slf4j
public class ChatClientConfig {

    /**
     * 配置ChatClient
     * 在仅引入一种AI底层模型类型依赖时使用, 在仅引入一种AI底层模型类型依赖时spring会自动配置ChatClient.Builder
     * 若同时引入多个依赖，则不能使用该方法，使用{@link ChatClientConfig#chatClient(DeepSeekChatModel)}代替
     *
     * @param builder 建设者
     * @return {@link ChatClient }
     */
//    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }

    
    /**
     * 配置ChatClient
     * 并指定默认System内容
     * 引入多种AI底层模型类型依赖时使用，
     * 此时不能直接使用ChatClient.Builder和ChatModel的抽象，需要指定对应的依赖ChatModel实现
     * 如： DeepSeekChatModel 或 OpenAiChatModel
     *
     * @param chatModel spring配置文件自动配置的ChatModel，
     * @return {@link ChatClient }
     */
    @Bean
    public ChatClient chatClient(DeepSeekChatModel chatModel) {
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        return builder
                .defaultSystem("""
                        你是一个以中文为主的模型
                        即使用户输入英文, 所有回答也均采用中文回答
                        并尽量产出中文为主的内容
                        """
                ) // 默认的system提示词, 当未输入system时采用
                .build();
    }
    
    /**
     * 配置ChatClient
     * 具体模型和配置取决于application-openai.yml配置文件
     *
     * @param chatModel 聊天模型
     * @return {@link ChatClient }
     */
//    @Bean
    public ChatClient openAiChatClient(OpenAiChatModel chatModel) {
        return ChatClient.create(chatModel);
    }

    /**
     * 配置ChatClient
     * 具体模型和配置取决于application-deepseek.yml配置文件
     *
     * @param chatModel 聊天模型
     * @return {@link ChatClient }
     */
//    @Bean
    public ChatClient deepSeekChatClient(DeepSeekChatModel chatModel) {
        // 引入多种AI底层模型类型依赖时使用
        // 创建ChatClient实例，使用DeepSeekChatModel作为底层模型
        return ChatClient.create(chatModel);
    }

    /**
     * 多平台聊天客户端
     *
     * @param openAiChatClient   打开ai聊天客户端
     * @param deepSeekChatClient 深度搜索聊天客户端
     * @return {@link CommandLineRunner } 启动时运行
     */
//    @Bean
    public CommandLineRunner runMultipleModel(ChatClient openAiChatClient, ChatClient deepSeekChatClient) {
        return args -> {
            var scanner = new Scanner(System.in);
            ChatClient chat;

            // Model selection
            System.out.println("\nSelect your AI model:");
            System.out.println("1. OpenAI");
            System.out.println("2. DeepSeek");
            System.out.print("Enter your choice (1 or 2): ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                chat = openAiChatClient;
                System.out.println("Using OpenAI model");
            } else {
                chat = deepSeekChatClient;
                System.out.println("Using DeepSeek model");
            }
            while (true) {
                System.out.print("\nEnter your question: (press 'q' to exit)");
                String input = scanner.nextLine();
                if (input.equals("q")) {
                    break;
                }
                // Use the selected chat client
                String response = chat.prompt(input).call().content();
                System.out.println("ASSISTANT:\n" + response);
            }
            scanner.close();
        };
    }


    /**
     * 创建多个不同的模型， 并使用
     * OpenAiApi 和 OpenAiChatModel 类提供了一个 mutate（） 方法，允许您创建具有不同属性的现有实例的变体。
     * 当您需要使用多个与 OpenAI 兼容的 API 时，这尤其有用。
     *
     * @param openAiApi       基本开放ai api
     * @param openAiChatModel 基本聊天模型
     * @return {@link CommandLineRunner } 启动时运行
     */
//    @Bean
    public CommandLineRunner multiClientFlow(OpenAiApi openAiApi, OpenAiChatModel openAiChatModel) {
        return args -> {
            try {
                // Derive a new OpenAiApi for Groq (Llama3)
                OpenAiApi groqApi = openAiApi.mutate()
                        .baseUrl("https://api.groq.com/openai")
                        .apiKey(System.getenv("GROQ_API_KEY"))
                        .build();

                // Derive a new OpenAiApi for OpenAI GPT-4
                OpenAiApi gpt4Api = openAiApi.mutate()
                        .baseUrl("https://api.openai.com")
                        .apiKey(System.getenv("OPENAI_API_KEY"))
                        .build();

                // Derive a new OpenAiChatModel for Groq
                OpenAiChatModel groqModel = openAiChatModel.mutate()
                        .openAiApi(groqApi)
                        .defaultOptions(OpenAiChatOptions.builder()
                                .model("llama3-70b-8192")
                                .temperature(0.5)
                                .build()
                        )
                        .build();

                // Derive a new OpenAiChatModel for GPT-4
                OpenAiChatModel gpt4Model = openAiChatModel.mutate()
                        .openAiApi(gpt4Api)
                        .defaultOptions(OpenAiChatOptions
                                .builder().model("gpt-4")
                                .temperature(0.7)
                                .build()
                        )
                        .build();

                // Simple prompt for both models
                String prompt = "What is the capital of France?";

                String groqResponse = ChatClient.builder(groqModel).build().prompt(prompt).call().content();
                String gpt4Response = ChatClient.builder(gpt4Model).build().prompt(prompt).call().content();

                log.info("Groq (Llama3) response: {}", groqResponse);
                log.info("OpenAI GPT-4 response: {}", gpt4Response);
            } catch (Exception e) {
                log.error("Error in multi-client flow", e);
            }
        };
    }


}
