package com.example.ai.t12rag;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Spring将RAG分为以下4个模块
 * <p>
 * Pre-Retrieval  预检索
 * <p>
 * Retrieval  检索
 * <p>
 * Post-Retrieval  检索后处理
 * <p>
 * Generation  结果生成
 * <p>
 * 这4个模块中的组件可以和RetrievalAugmentationAdvisor自由结合使用
 * 
 * @see Rag11PreRetrieval01QueryTransformer
 * @see Rag12Retrieval
 * @see Rag13PostRetrieval
 * @see Rag14Generation
 * @author bootystar
 */
@SpringBootTest
public class Rag10 {

    /**
     * 自定义
     * 结合使用
     */
    @Test
    void custom() {
        
        // QueryTransformer
        var rewriteQueryTransformer = RewriteQueryTransformer.builder().build();
        var compressionQueryTransformer = CompressionQueryTransformer.builder().build();
        var translationQueryTransformer = TranslationQueryTransformer.builder().build();

        // QueryExpander
        var multiQueryExpander = MultiQueryExpander.builder().build();

        // DocumentRetriever
        var vectorStoreDocumentRetriever = VectorStoreDocumentRetriever.builder().build();

        // QueryAugmenter 
        var contextualQueryAugmenter = ContextualQueryAugmenter.builder().build();
        
        Advisor advisor = RetrievalAugmentationAdvisor.builder()
                // pre-retrieval 预检索
                .queryTransformers(rewriteQueryTransformer) // 查询转化器
                .queryExpander(multiQueryExpander) // 查询扩展器
                
                // retrieval 检索
                .documentRetriever(vectorStoreDocumentRetriever) // 文档检索器
                
//                .documentJoiner() // 文档连接器
                // post-retrieval 检索后处理
//                .documentPostProcessor() // 文档后处理器
                
                // generation 结果生成
                .queryAugmenter(contextualQueryAugmenter) // 查询增强器
                
                .build();

    }
    
}
