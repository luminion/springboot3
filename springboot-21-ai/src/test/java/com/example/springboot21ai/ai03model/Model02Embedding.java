package com.example.springboot21ai.ai03model;

import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 文本嵌入模型
 * 文本嵌入用于衡量文本字符串的相关性。嵌入是一个浮点数列表（向量）。
 * 两个向量之间的距离衡量它们的相关性。距离小表示高度相关，距离大表示低度相关
 *
 * @author bootystar
 */
@SpringBootTest
public class Model02Embedding {
    @Autowired
    private OpenAiEmbeddingModel embeddingModel;

    @Test
    void embeddingTest() {
        var worldsList = List.of("Hello World", "World is big and salvation is near");

        var embeddingOptions = OpenAiEmbeddingOptions.builder()
//                .model("Different-Embedding-Model-Deployment-Name") // 嵌入模型的名称
                .build();
        var embeddingRequest = new EmbeddingRequest(worldsList, embeddingOptions);
        EmbeddingResponse embeddingResponse = embeddingModel
                .call(embeddingRequest);
        System.out.println(embeddingResponse);
        // 该数据就是模型的向量结果
        var results = embeddingResponse.getResults();
        results.stream()
                .map(Embedding::getMetadata)
                .forEach(e -> System.out.println(e))
        ;
    }


}
