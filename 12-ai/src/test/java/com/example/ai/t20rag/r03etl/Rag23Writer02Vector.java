package com.example.ai.t20rag.r03etl;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.writer.FileDocumentWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 使用向量保存文档
 * 向量的配置和基础使用见:{@link com.example.ai.t20rag.r01base.Rag02Vector}
 * 
 * @see Rag02Vector
 * @author bootystar
 */
@SpringBootTest
public class Rag23Writer02Vector {
    @Autowired
    private VectorStore vectorStore;
    
    @Test
    public void test(){
        Document doc1 = new Document("java AI wonderful!! java AI wonderful!! java AI wonderful!! java AI wonderful!! java AI wonderful!!");
        Document doc2 = new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!");
        var documents = List.of(doc1, doc2);
        vectorStore.add(documents);
    }
    
}
