package com.example.ai.t20rag.r02module;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Generation 生成模块
 * <p>
 * 负责根据用户查询和检索到的文档生成最终响应, 一个用于根据用户查询和检索到的文档生成最终响应的组件。
 * 可以在此处截断结果或做出处理
 * 包含以下组件:
 * <p>
 * QueryAugmenter 查询扩增器
 * <p>
 * 一个用于根据用户查询和检索到的文档生成最终响应的组件。
 *
 * @author bootystar
 */
@SpringBootTest
public class Rag14Generation01QueryAugmenter {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private ChatClient.Builder chatClientBuilder;

    /**
     * ContextualQueryAugmenter 通过提供的文档内容中的上下文数据来增强用户查询
     * 默认情况下， ContextualQueryAugmenter 不允许检索到的上下文为空。当出现这种情况时，它会指示模型不要回答用户的问题
     * 您可以启用 allowEmptyContext 选项，以允许模型在检索到的上下文为空时生成响应
     */
    @Test
    void contextualQueryAugmenter() {
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .build()
                )
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true) // 允许空上下文
                        .build()
                )
                .build();
        String question = "which type ai is good?";
        String answer = chatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .user(question)
                .call()
                .content();
        System.out.println(answer);
    }

}
