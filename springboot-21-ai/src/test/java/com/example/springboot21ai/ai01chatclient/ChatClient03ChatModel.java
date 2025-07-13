package com.example.springboot21ai.ai01chatclient;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 手动创建模型
 * 根据对应模型的Api和ChatModel
 * 手动创建不同模型的实例
 * 可以指定多个不同云平台,不同的模型 
 * 
 * @author bootystar
 */
@Slf4j
@SpringBootTest
public class ChatClient03ChatModel {
    
    @Autowired
    @Qualifier("aliApi")
    private OpenAiApi aliApi;
    @Autowired
    @Qualifier("siliconFlowApi")
    private OpenAiApi siliconFlowApi;
    @Autowired
    @Qualifier("baseChatModel")
    private OpenAiChatModel chatModel;
    
    @Test
    void createCustomChatModel(){
        // Simple prompt for both models
        String prompt = "你是哪个运行在哪个大模型云服务商平台的哪个模型,参数是多少?";
        
        // 创建阿里百炼的模型
        OpenAiChatModel qwenPlus = chatModel
                .mutate() // 创建一个配置完全一样的新chatModel
                .openAiApi(aliApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("qwen-plus") //指定模型
                        .temperature(0.5) // 随机度
                        .build()
                )
                .build();
        
        // 创建硅基流动的模型
        OpenAiChatModel deepSeekR1 = chatModel
                .mutate() // 创建一个配置完全一样的新chatModel
                .openAiApi(siliconFlowApi)
                .defaultOptions(
                        OpenAiChatOptions.builder()
                        .model("deepseek-ai/DeepSeek-R1-0528-Qwen3-8B")//指定模型
                        .temperature(0.7) // 随机度
                        .build()
                )
                .build();

        // 使用阿里百炼的模型
        String aliResponse = ChatClient.builder(qwenPlus).build().prompt(prompt).call().content();
        log.info("ali response:\n{}\n", aliResponse);
        
        // 使用硅基流动的模型
        String siliconResponse = ChatClient.builder(deepSeekR1).build().prompt(prompt).call().content();
        log.info("silicon response:\n{}\n", siliconResponse);
    }
}
