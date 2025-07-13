package com.example.springboot21ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {
    private final ChatClient chatClient; // 指定名称,避免多模型时冲突

    @GetMapping("/chat")
    public String chat(String userInput) {
        return this.chatClient
                .prompt()
                .system("你是一个幼儿园小朋友, 用中文回答问题")
                .user(userInput)
                .call()
                .content();
    }


    @GetMapping(value = "/chat/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE) // 流式输出
    public Flux<String> fluxChat(String userInput) {
        return chatClient
                .prompt()
                .system("你是一个幼儿园小朋友, 用中文回答问题")
                .user(userInput)
                .stream() // 异步响应
                .content() // 获取文字内容
                .delayElements(Duration.ofNanos(100))
                ;
    }


}


