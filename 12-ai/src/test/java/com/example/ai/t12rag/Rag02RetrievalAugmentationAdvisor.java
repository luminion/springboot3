package com.example.ai.t12rag;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Retrieval Augmented Generation
 * 检索增强生成（RAG）是一种技术，有助于克服大型语言模型的局限性，这些模型在处理长文本内容、事实准确性和上下文感知方面存在困难
 * Spring AI 提供开箱即用的支持，用于常见 RAG 流程，使用 Advisor API
 * 要使用RetrievalAugmentationAdvisor, 您需要将 spring-ai-rag 依赖项添加到您的项目
 * <p>
 * RetrievalAugmentationAdvisor
 * Spring AI 包含一组 RAG 模块，您可以使用它们来构建自己的 RAG 工作流。
 * RetrievalAugmentationAdvisor 是一个 Advisor ，基于模块化架构，为最常见的 RAG 工作流提供开箱即用的实现
 *
 * @author bootystar
 */
@SpringBootTest
public class Rag02RetrievalAugmentationAdvisor {

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private ChatClient.Builder chatClientBuilder;


    /**
     * RetrievalAugmentationAdvisor 基础使用示例
     * 默认情况下， RetrievalAugmentationAdvisor 不允许检索到的上下文为空
     */
    @Test
    void retrievalAugmentationAdvisor() {
        String question = "which type ai is good?";
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .build())
                .build();

        String answer = chatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .user(question)
                .call()
                .content();
        System.out.println(answer);
    }

    /**
     * 默认情况下， RetrievalAugmentationAdvisor 不允许检索到的上下文为空。
     * 当发生这种情况时，它会指示模型不要回答用户的问题。您可以按以下方式允许空上下文
     */
    @Test
    void retrievalAugmentationAdvisor2() {
        String question = "which type ai is good?";
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true) // 允许空上下文
                        .build())
                .build();

        String answer = chatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .user(question)
                .call()
                .content();
        System.out.println(answer);
    }

    /**
     * VectorStoreDocumentRetriever 接受一个 FilterExpression 来根据元数据过滤搜索结果。
     * 您可以在实例化 VectorStoreDocumentRetriever 时提供，或在运行时按请求使用 FILTER_EXPRESSION 顾问上下文参数
     */
    @Test
    void test3() {
        String question = "which type ai is good?";
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50) // 相似度阈值
                        .vectorStore(vectorStore)
                        .build())
                .build();

        String answer = chatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .advisors(a -> a.param(VectorStoreDocumentRetriever.FILTER_EXPRESSION, "author == 'bootystar'")) // 运行时过滤
                .user(question)
                .call()
                .content();
        System.out.println(answer);
    }

    /**
     * RewriteQueryTransformer会对传入的语句进行重写, 将其转换为更适合检索任务的语句
     */
    @Test
    void test4() {
        String question = "which type ai is good?";
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(RewriteQueryTransformer.builder()
                        .chatClientBuilder(chatClientBuilder.build().mutate())
                        .build()) // 重写查询转换器
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .build())
                .build();

        String answer = chatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .user(question)
                .call()
                .content();
        System.out.println(answer);
    }


}
