package com.example.ai.t20rag.r04advisor;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * RetrievalAugmentationAdvisor
 * Spring AI 包含一组 RAG 模块，您可以使用它们来构建自己的 RAG 工作流。
 * RetrievalAugmentationAdvisor 是一个 Advisor ，基于模块化架构，为最常见的 RAG 工作流提供开箱即用的实现
 * 该advisor可以结合rag各个模块的transformer, retriever, augmenter等组件使用
 *
 * @author bootystar
 */
@SpringBootTest
public class Rag52RetrievalAugmentationAdvisor {

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


    /**
     * 组合使用, 查看效果
     * 提前设置日志级别:
     * org.springframework.ai.rag.advisor: DEBUG
     * 
     */
    @Test
    void custom() {
        // QueryTransformer
        var rewriteQueryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
        var compressionQueryTransformer = CompressionQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
        var translationQueryTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();

        // QueryExpander
        var multiQueryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();

        // DocumentRetriever
        var vectorStoreDocumentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .build();

        // QueryAugmenter 
        var contextualQueryAugmenter = ContextualQueryAugmenter.builder()
                .allowEmptyContext(true)
                .build();

        Advisor advisor = RetrievalAugmentationAdvisor.builder()
                // pre-retrieval 预检索
                .queryTransformers(rewriteQueryTransformer,compressionQueryTransformer,translationQueryTransformer) // 查询转化器
                .queryExpander(multiQueryExpander) // 查询扩展器
                // retrieval 检索
                .documentRetriever(vectorStoreDocumentRetriever) // 文档检索器
                // post-retrieval 检索后处理
                .documentPostProcessors((query, documents) -> documents) // 文档后处理器
                // generation 结果生成
                .queryAugmenter(contextualQueryAugmenter) // 查询增强器
                .build();

        String question = "which type ai is good?";
        var response = chatClient.prompt()
                .advisors(advisor)
                .user(question)
                .call()
                .chatResponse();
        
   
        System.out.println( response.getResult().getOutput().getText());

    }

}
