package com.example.springboot21ai.ai02prompt;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * OpenAiApi 提供的是一个轻量级的 Java 客户端
 * 该类提供较为封装不完全的， 原始的调用方式
 * 使用较为麻烦，但操作性较高
 * 
 * @author bootystar
 */
@SpringBootTest
public class Prompt03OpenAiApi {
    
    @Autowired
    private OpenAiApi openAiApi;
    
    @Test
    @SneakyThrows
    void test(){
        // 手动创建
//        OpenAiApi openAiApi = OpenAiApi.builder()
//                .apiKey(System.getenv("OPENAI_API_KEY"))
//                .build();

        OpenAiApi.ChatCompletionMessage chatCompletionMessage =
                new OpenAiApi.ChatCompletionMessage("Hello world", OpenAiApi.ChatCompletionMessage.Role.USER);

        // Sync request
        var response = openAiApi.chatCompletionEntity(
                new OpenAiApi.ChatCompletionRequest(
                        List.of(chatCompletionMessage), 
                        "gpt-3.5-turbo", 
                        0.8, 
                        false)
        );
        var list = response.getBody().choices().stream()
                .map(e -> e.message().content()).toList();
        System.out.println(String.join(" ",list));

        System.out.println("==================");

        // Streaming request
        Flux<OpenAiApi.ChatCompletionChunk> streamResponse = openAiApi.chatCompletionStream(
                new OpenAiApi.ChatCompletionRequest(
                        List.of(chatCompletionMessage), 
                        "gpt-3.5-turbo", 
                        0.8, 
                        true)
        );
        var list1 = streamResponse.toStream()
                .flatMap(e -> e.choices().stream())
                .map(e -> e.delta().content())
                .toList()
                ;
        System.out.println(String.join(" ",list1));
        
    }
}
