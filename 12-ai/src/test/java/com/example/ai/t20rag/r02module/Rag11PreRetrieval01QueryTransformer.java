package com.example.ai.t20rag.r02module;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * QueryTransformer 查询转换
 * 一个用于将输入查询转换为更适合检索任务的组件，解决诸如查询语句不完整、术语模糊、词汇复杂或不支持的语言等挑战。
 * <p>
 * 有以下三个实现
 * <p>
 * CompressionQueryTransformer 压缩查询转换器
 * <p>
 * RewriteQueryTransformer 重写查询转换器
 * <p>
 * TranslationQueryTransformer 翻译查询转换器
 * <p>
 * 在使用 QueryTransformer 时，建议将 ChatClient.Builder 配置为低temperature（例如，0.0），以确保更确定和准确的结果，从而提高检索质量。
 * 大多数聊天模型的默认temperature对于优化的查询转换来说通常过高，导致检索效果降低。
 *
 * @author bootystar
 */
@SpringBootTest
public class Rag11PreRetrieval01QueryTransformer {

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @BeforeEach
    void init() {
        // 设置temperature为0 , 降低随机度
        var chatOptions = ChatOptions.builder()
                .temperature(0.0)
                .build();
        chatClientBuilder.defaultOptions(chatOptions);
    }


    /**
     * CompressionQueryTransformer 压缩查询转换器
     * 使用大型语言模型将对话历史和后续查询压缩成一个独立的查询，该查询能够捕捉对话的精髓
     * 当对话历史较长且后续查询与对话上下文相关时，这个转换器很有用
     * 该组件使用的提示可以通过构建器中的 promptTemplate() 方法进行自定义
     * 此处建议使用能力较强的模型, 若模型能力差, 可能结果不准确
     */
    @Test
    void compressionQueryTransformer() {
        Query query = Query.builder()
                .text("And what is its second largest city?")
                .history(new UserMessage("What is the capital of Denmark?"),
                        new AssistantMessage("Copenhagen is the capital of Denmark."))
                .build();
        QueryTransformer queryTransformer = CompressionQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();

        Query transformedQuery = queryTransformer.transform(query);
        System.out.println(transformedQuery.text());
        /*
         压缩前
         user: What is the capital of Denmark?
         assistant: Copenhagen is the capital of Denmark.
         user: And what is its second largest city?
         压缩后
         user: What is the second largest city in Denmark?
         */
    }


    /**
     * RewriteQueryTransformer  重写查询转换器
     * 使用大型语言模型来重写用户查询，以便在查询目标系统（如向量存储或网络搜索引擎）时提供更好的结果
     * 当用户查询内容冗长、模糊或包含可能影响搜索结果质量的不相关信息时，这个转换器很有用
     * 该组件使用的提示可以通过构建器中的 promptTemplate() 方法进行自定义
     */
    @Test
    void rewriteQueryTransformer() {
        Query query = new Query("I'm studying machine learning. What is an LLM?");

        QueryTransformer queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
        Query transformedQuery = queryTransformer.transform(query);

        System.out.println(transformedQuery.text());
        /*
         重写前
         I'm studying machine learning. What is an LLM?
         重写后
         Definition and explanation of LLM in the context of machine learning
         */
    }


    /**
     * TranslationQueryTransformer 翻译查询转化器
     * 使用大型语言模型将查询翻译为目标语言，该目标语言是用于生成文档嵌入的嵌入模型所支持的。
     * 如果查询已经是目标语言，则不变返回。如果查询的语言未知，也保持不变返回
     * 当嵌入模型在特定语言上训练而成，而用户查询使用的是不同语言时，这个转换器很有用
     * 该组件使用的提示可以通过构建器中的 promptTemplate() 方法进行自定义
     */
    @Test
    void translationQueryTransformer() {
        Query query = new Query("Hvad er Danmarks hovedstad?");

        QueryTransformer queryTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .targetLanguage("english")
                .build();

        Query transformedQuery = queryTransformer.transform(query);

        System.out.println(transformedQuery.text());
        /*
          翻译前
          Hvad er Danmarks hovedstad?
          翻译后
          What is the capital of Denmark?
         */
    }

}
