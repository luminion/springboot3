package com.example.ai.t11vector;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Vector Databases  向量数据库
 * Spring AI 通过 VectorStore 接口提供了一种抽象化的 API，用于与向量数据库进行交互
 * 要向向量数据库中插入数据，请将其封装在 Document 对象中。
 * Document 类封装来自数据源（如 PDF 或 Word 文档）的内容，并包含表示为字符串的文本。它还包含键值对形式的元数据，包括文件名等详细信息
 * 在向量数据库中插入时，文本内容通过嵌入模型转换为数值数组，即 float[] 向量嵌入。
 * 嵌入模型，如 Word2Vec、GLoVE 和 BERT，或 OpenAI 的 text-embedding-ada-002 ，用于将单词、句子或段落转换为这些向量嵌入
 * 接口中的 similaritySearch 方法允许获取与给定查询字符串相似的文档。这些方法可以通过使用以下参数进行微调
 * <p>
 * k : 一个整数，用于指定返回的相似文档的最大数量。这通常被称为“top K”搜索，或“K 最近邻”（KNN）
 * threshold : 一个介于 0 到 1 之间的双精度值，值越接近 1 表示相似度越高。默认情况下，例如，如果你设置阈值为 0.75，那么只有相似度高于此值的文档才会被返回
 * Filter.Expression : 一个用于传递类似 SQL 中“where”子句的函数式 DSL（领域特定语言）表达式的类，但它仅适用于 Document 的元数据键值对
 * filterExpression : 基于 ANTLR4 的外部 DSL，它接受字符串形式的过滤表达式。例如，对于 country、year 和 isActive 等元数据键，你可以使用类似 country == 'UK' && year >= 2020 && isActive == true. 的表达式
 * <p>
 * 某些向量存储需要在使用前初始化其后端模式。
 * 默认情况下，它不会为你自动初始化。你必须主动选择，通过为相应的构造函数参数传递 boolean ，
 * 或者在使用 Spring Boot 时，在 application.properties 或 application.yml 中将相应的 initialize-schema 属性设置为 true
 * 配置文件参考: application-vector-pg.yml
 * <p>
 * 也可以使用手动配置, 配置多个向量数据库 : {@link com.example.ai.config.VectorStoreConfig#vectorStore(JdbcTemplate, EmbeddingModel)}
 * <p>
 * 安装pg见pgvector.bat, 需要下载visual studio配置c++桌面开发插件, 并使用cmd 运行
 * pgvector.sql为pg的初始化语句, 指定initialize-schema为true时会默认运行
 *
 * @author bootystar
 */
@SpringBootTest
public class Vector01 {

    @Autowired
    private VectorStore vectorStore;


    /**
     * 新增向量数据
     */
    @Test
    void insertVectorStore() {
        /*
        Document 的 metadata 是用于存储文档的附加信息，它是一个键值对（Map<String, Object>）结构，
        通常用于保存文档的元数据（metadata），比如文档的来源、类型、创建时间等非内容信息
         */
        List<Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("type", "spring", "author", "bootystar")),
                new Document("java AI wonderful!! java AI wonderful!! java AI wonderful!! java AI wonderful!! java AI wonderful!!", Map.of("type", "java", "author", "bootystar")),
                new Document("python AI brilliant!! python AI brilliant!! python AI brilliant!! python AI brilliant!! python AI brilliant!!", Map.of("type", "python", "author", "booty")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2"))
        );

        // Add the documents to PGVector
        vectorStore.add(documents);
    }

    /**
     * 往向量数据库插入数据,
     * 并检索
     */
    @Test
    void queryVectorStore() {
        // Retrieve documents similar to a query
        List<Document> results = this.vectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(5).build());
        // print
        results.forEach(e -> System.out.println(e));
    }

    @Test
    void removeVectorStore() {
        vectorStore.delete("");
    }


}
