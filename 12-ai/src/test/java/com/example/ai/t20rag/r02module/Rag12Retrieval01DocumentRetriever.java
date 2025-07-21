package com.example.ai.t20rag.r02module;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.join.DocumentJoiner;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

/**
 * Retrieval 检索模块
 * <p>
 * 负责查询数据系统（如向量存储）并检索最相关的文档。
 * <p>
 * 有以下2个模块:
 * Document Search  文档搜索
 * 负责从底层数据源（如搜索引擎、向量存储、数据库或知识图谱）中检索 Documents 的组件
 * Document Join  文档连接
 * 用于将基于多个查询从多个数据源检索到的文档组合成一个文档集合。在合并过程中，它还可以处理重复文档和互惠排名策略。
 *
 * @author bootystar
 */
@SpringBootTest
public class Rag12Retrieval01DocumentRetriever {

    @Autowired
    private VectorStore vectorStore;
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
     * VectorStoreDocumentRetriever
     * 从向量存储中检索与输入查询语义相似的文档。它支持基于元数据的过滤、相似度阈值和前 k 个结果。
     * 过滤表达式可以是静态的或动态的。对于动态过滤表达式，您可以传递一个 Supplier
     * 您也可以通过 Query API 使用 FILTER_EXPRESSION 参数提供请求特定的过滤表达式。
     * 如果同时提供了请求特定的和检索器特定的过滤表达式，请求特定的过滤表达式将优先
     */
    @Test
    void vectorStoreDocumentRetriever() {
        DocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(0.73)
                .topK(5)
                .filterExpression(new FilterExpressionBuilder()
                        .eq("author", "bootystar")
                        .build())
                .build();
        Query query = Query.builder()
                .text("What is the best ai?")
//                .context(Map.of(VectorStoreDocumentRetriever.FILTER_EXPRESSION, "author == 'booty'")) // 过滤表达式
                .build();
        List<Document> documents = retriever.retrieve(query);
        System.out.printf("===================%s found========================%n", documents.size());
        for (var document : documents) {
            System.out.println("====================document=======================");
            System.out.println(document.getFormattedContent());
        }
    }


    /**
     * ConcatenationDocumentJoiner
     * 通过将基于多个查询和来自多个数据源检索到的文档连接起来，组合成一个文档集合。如果存在重复文档，则保留第一次出现的文档。每个文档的分数保持不变。
     */
    @Test
    void documentJoin() {
        DocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .build();
        Query query1 = Query.builder()
                .text("What is the best ai?")
                .context(Map.of(VectorStoreDocumentRetriever.FILTER_EXPRESSION, "author == 'bootystar'")) // 过滤表达式
                .build();
        Query query2 = Query.builder()
                .text("What is the best ai?")
                .context(Map.of(VectorStoreDocumentRetriever.FILTER_EXPRESSION, "author == 'booty'")) // 过滤表达式
                .build();
        List<Document> documents1 = retriever.retrieve(query1);
        List<Document> documents2 = retriever.retrieve(query2);


        Map<Query, List<List<Document>>> documentsForQuery = Map.of(
                query1, List.of(documents1),
                query2, List.of(documents2)
        );
        DocumentJoiner documentJoiner = new ConcatenationDocumentJoiner();
        List<Document> documents3 = documentJoiner.join(documentsForQuery);
        System.out.printf("===================%s found in query1========================%n", documents1.size());
        System.out.printf("===================%s found in query2========================%n", documents2.size());
        System.out.printf("===================%s found after join========================%n", documents3.size());
        for (var document : documents3) {
            System.out.println("====================document=======================");
            System.out.println(document.getFormattedContent());
        }
    }


}
