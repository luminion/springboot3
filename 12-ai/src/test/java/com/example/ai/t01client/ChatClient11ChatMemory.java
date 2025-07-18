package com.example.ai.t01client;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * 大型语言模型 （LLM） 是无状态的，这意味着它们不会保留有关以前交互的信息。
 * 当您希望在多个交互中维护上下文或状态时，这可能是一个限制。
 * 为了解决这个问题，Spring AI 提供了聊天内存功能，允许您在与 LLM 的多次交互中存储和检索信息
 * <p>
 * ChatMemory 抽象允许您实现各种类型的内存以支持不同的使用案例。
 * 消息的底层存储由 ChatMemoryRepository 处理，其唯一职责是存储和检索消息。
 * 由 ChatMemory 实现决定要保留哪些消息以及何时删除它们。策略示例可能包括保留最后 N 条消息、将消息保留一段时间或将消息保持在某个令牌限制内
 * 在选择内存类型之前，必须了解聊天内存和聊天记录之间的区别
 * <p>
 * Chat Memory 大型语言模型保留并用于在整个对话中保持上下文感知的信息
 * Chat History 整个会话历史记录，包括用户与模型之间交换的所有消息
 * <p>
 * ChatMemory 抽象旨在管理聊天内存 。它允许您存储和检索与当前对话上下文相关的消息。但是，它并不是存储聊天记录的最佳选择。
 * 如果您需要维护所有交换消息的完整记录，您应该考虑使用不同的方法，例如依靠 Spring Data 来有效存储和检索完整的聊天历史记录
 * <p>
 * Spring AI 会自动配置一个 ChatMemory bean，您可以直接在应用程序中使用。
 * 默认情况下，它使用内存存储库来存储消息 （InMemoryChatMemoryRepository），并使用 MessageWindowChatMemory 实现来管理对话历史记录。
 * 如果已经配置了不同的存储库（例如，Cassandra、JDBC 或 Neo4j），Spring AI 将改用该存储库
 * <p>
 * ChatMemory 类型
 * <p>
 * MessageWindowChatMemory
 * 默认配置的 ChatMemory 实现。
 * 将消息窗口维护到指定的最大大小。当消息数超过最大值时，将删除较旧的消息，同时保留系统消息。默认窗口大小为 20 封邮件
 * 如果添加了新的系统消息，则所有以前的系统消息都将从内存中删除。
 * 这可确保最新的上下文始终可用于会话，同时保持内存使用受限
 * @author bootystar
 */
@SpringBootTest
public class ChatClient11ChatMemory {

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatMemory chatMemory;

    /**
     * 使用默认的MessageWindowChatMemory记录输入内容
     */
    @Test
    void defaultMessageWindowChatMemory() {
        var messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .build();
        var response = chatClient.prompt()
                .advisors(
                        messageChatMemoryAdvisor
                )
                .user("我是李四,男, 大学生")
                .call()
                .content();
        System.out.println(response);

        var response2 = chatClient.prompt()
                .advisors(
                        messageChatMemoryAdvisor
                )
                .user("你知道我是谁吗")
                .call()
                .content();
        System.out.println(response2);
    }


    /**
     * 使用自定义的MessageWindowChatMemory,限制最记忆数量
     * 需要注意模型的上下文长度,内容超过长度后会报错
     */
    @Test
    @SneakyThrows
    void useMessageWindowChatMemory() {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(5) // 记忆的最大信息
                .build();
        var messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .build();
        String prompt = "你是个帮助记忆的工具,如果用户向你发送的信息,就记住它并返回给用户, 不要说多的话语,如果用户问了问题就回答, ";
        int count = 10;
        var latch = new CountDownLatch(count);
        IntStream.range(0, count)
                .parallel() // 并发
                .forEach(i -> {
                    var atomicInteger = new AtomicInteger(i);
                    var content = chatClient
                            .prompt(prompt)
                            .advisors(messageChatMemoryAdvisor)
                            .user(u -> u
                                    .text("有一个程序员是张{index}, 男, {index}岁, 程序员")
                                    .param("index", atomicInteger)
                            )
                            .call()
                            .content();
                    System.out.println(content);
                    latch.countDown();
                });
        latch.await();
        var content = chatClient
                .prompt(prompt)
                .advisors(messageChatMemoryAdvisor)
                .user(u -> u
                        .text("我一共向你发送了多少条信息, 第{index}条是什么")
                        .param("index", 3)
                )
                .call()
                .content();
        System.out.println(content);
    }




 


}
