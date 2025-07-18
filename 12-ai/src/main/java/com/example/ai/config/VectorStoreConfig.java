package com.example.ai.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 向量储存手动配置类
 * @author bootystar
 */
@Configuration(proxyBeanMethods = false)
public class VectorStoreConfig {

    /**
     * 手动添加PgVectorStore
     * 此时不要引入自动配置依赖, 手动引入
     * spring-ai-pgvector-store
     * 同时若其他依赖未引入jdbc和pg驱动需要引入依赖:
     * spring-boot-starter-jdbc
     * postgresql
     *
     * @param jdbcTemplate   jdbc模板
     * @param embeddingModel 嵌入模型
     * @return {@link VectorStore }
     */
//    @Bean
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(1536)                    // Optional: defaults to model dimensions or 1536
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)       // Optional: defaults to COSINE_DISTANCE
                .indexType(PgVectorStore.PgIndexType.HNSW)                     // Optional: defaults to HNSW
                .initializeSchema(true)              // Optional: defaults to false
                .schemaName("public")                // Optional: defaults to "public"
                .vectorTableName("vector_store")     // Optional: defaults to "vector_store"
                .maxDocumentBatchSize(10000)         // Optional: defaults to 10000
                .build();
    }
}
