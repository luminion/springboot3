package com.example.springboot21ai.ai01chatclient;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
//import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
//import org.springframework.ai.chat.memory.repository.jdbc.MysqlChatMemoryRepositoryDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
//import org.springframework.jdbc.core.JdbcTemplate;

/**
 * ChatMemoryRepository 类型
 * <p>
 * InMemoryChatMemoryRepository
 * 未配置时的默认存储库实现。如果已经配置了不同的存储库（例如，Cassandra、JDBC 或 Neo4j），Spring AI 将改用该存储库
 * 使用 ConcurrentHashMap 将消息存储在内存中
 * <p>
 * JdbcChatMemoryRepository
 * 是一个内置实现，它使用 JDBC 将消息存储在关系数据库中。它支持多个开箱即用的数据库，适用于需要持久存储聊天内存的应用程序
 * 1.首先，将以下依赖项添加到您的项目中
 * <pre>{@code
 * <dependency>
 *     <groupId>org.springframework.ai</groupId>
 *     <artifactId>spring-ai-starter-model-chat-memory-repository-jdbc</artifactId>
 * </dependency>
 * }</pre>
 * 2.添加对应数据库驱动
 * <p>
 * 3.修改配置文件, 例如: application-chat-memory.yml
 * <p>
 * 完成以上操作后,会自动配置对应的JdbcChatMemoryRepository
 * <p>
 * 还有其他多个不常用的实现, 使用方式基本一致
 * 参考 <a href="https://docs.spring.io/spring-ai/reference/api/chat-memory.html">Spring AI ChatMemory文档</a>
 *
 * @author bootystar
 */
@SpringBootTest
public class ChatClient12ChatMemoryRepository {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ChatMemory chatMemory;
//    @Autowired
//    private JdbcTemplate jdbcTemplate;

    /**
     * 添加了JdbcChatMemoryRepository后,会替换默认的InMemoryChatMemoryRepository
     * 可以通过删除/添加依赖来验证
     */
    @Test
    void testJdbcChatMemoryRepository() {
        var chatMemoryRepository = applicationContext.getBean(ChatMemoryRepository.class);
        System.out.println("当前ChatMemory类型: " + chatMemory.getClass());
        System.out.println("当前ChatMemoryRepository类型: " + chatMemoryRepository.getClass());
    }

    /**
     * 使用jdbcTemplate创建定义的JdbcChatMemoryRepository
     */
    @Test
    void createCustomJdbcChatMemoryRepository() {
//        ChatMemoryRepository chatMemoryRepository = JdbcChatMemoryRepository.builder()
//                .jdbcTemplate(jdbcTemplate)
//                .dialect(new MysqlChatMemoryRepositoryDialect()) // 指定数据库语法
//                .build();
        // 之后可以将改bean注入到系统中使用
    }


}
