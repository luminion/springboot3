package com.example.ai.t20rag.r02module;

import org.springframework.boot.test.context.SpringBootTest;

/**
 * Spring将RAG分为以下4个模块
 * <p>
 * Pre-Retrieval        预检索
 * ---QueryTransformer  查询转换器
 * ---QueryExpansion    查询扩展器
 * Retrieval            检索
 * ---DocumentSearch    文档搜索
 * ---DocumentJoin      文档连接
 * Post-Retrieval       预检索
 * ---DocumentPostProcessor 文档后处理器
 * Generation  结果生成
 * 这4个模块中的组件可以和RetrievalAugmentationAdvisor自由结合使用
 * 
 * @see Rag11PreRetrieval01QueryTransformer
 * @see Rag12Retrieval01DocumentRetriever
 * @see Rag13PostRetrieval01DocumentPostProcessor
 * @see Rag14Generation01QueryAugmenter
 * @author bootystar
 */
@SpringBootTest
public class Rag10 {

    
    
}
