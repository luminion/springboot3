package com.example.ai.t12rag;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Query Expansion  查询扩展
 * <p>
 * 一个将输入查询扩展为查询列表的组件，通过提供替代查询形式或将复杂问题分解为更简单的子查询来解决诸如查询语句不完整等挑战。
 * <p>
 * 有以下一个实现
 * <p>
 * MultiQueryExpander 多查询扩展器
 *
 * @author bootystar
 */
@SpringBootTest
public class Rag11PreRetrieval02QueryExpander {

    @Autowired
    private ChatClient.Builder chatClientBuilder;


    /**
     * MultiQueryExpander
     * 使用大型语言模型将查询扩展为多个语义多样化的变体，以捕捉不同视角，有助于检索额外的上下文信息并增加找到相关结果的机会
     * 默认情况下， MultiQueryExpander 在扩展查询列表中包含原始查询。您可以通过构建器中的 includeOriginal 方法禁用此行为
     * 该组件使用的提示可以通过构建器中的 promptTemplate() 方法进行自定义
     */
    @Test
    void multiQueryExpander() {
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(chatClientBuilder)
                .numberOfQueries(3) // 扩展的查询数量, 
                .includeOriginal(false)
                .build();
        List<Query> queries = queryExpander.expand(new Query("How to run a Spring Boot app?"));
        queries.forEach(e -> System.out.println(e.text()));
        /*
        扩展前
        How do I start a Spring Boot application?
        扩展后
        How to set up and execute a Spring Boot application?
        What are the steps to launch a Spring Boot project in an IDE?
        Can you provide a guide on starting a Spring Boot application from the command line?
         */
    }


}
