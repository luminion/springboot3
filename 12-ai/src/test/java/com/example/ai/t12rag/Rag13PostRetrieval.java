package com.example.ai.t12rag;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Post-Retrieval 检索后处理模块
 * <p>
 * 负责处理检索到的文档，以实现最佳生成结果
 * 一个基于查询对检索到的文档进行后处理的组件，解决诸如信息丢失、模型上下文长度限制以及减少检索信息中的噪声和冗余等挑战。
 * 例如，它可以根据文档与查询的相关性进行排序，移除不相关或冗余的文档，或压缩每个文档的内容以减少噪音和冗余。
 * <p>
 * 包含以下一个组件:
 * DocumentPostProcessor 在将检索到的文档传递给模型之前对其进行后处理
 * <p>
 * 该接口无默认实现
 * 
 * @author bootystar
 */
@SpringBootTest
public class Rag13PostRetrieval {
    @Autowired
    private ChatClient.Builder chatClientBuilder;
    @Autowired
    private VectorStore vectorStore;

    /**
     * 使用 DocumentPostProcessor API 在将检索到的文档传递给模型之前对其进行后处理。
     * 例如，您可以使用此类接口根据文档与查询的相关性对检索到的文档进行重新排序，删除不相关或冗余的文档，或压缩每个文档的内容以减少噪声和冗余
     */
    @Test
    void vectorStoreDocumentRetriever() {
  
    }


}
