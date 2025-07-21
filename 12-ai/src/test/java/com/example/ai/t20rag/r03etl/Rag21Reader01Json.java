package com.example.ai.t20rag.r03etl;

import org.junit.jupiter.api.Test;
import org.springframework.ai.reader.JsonMetadataGenerator;
import org.springframework.ai.reader.JsonReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.Map;

/**
 * JsonReader 处理 JSON 文档，将它们转换为 Document 对象的列表
 * <p>
 * 配置选项:
 * resource : 指向 JSON 文件的 Spring Resource 对象
 * jsonKeysToUse : 应用于结果 Document 对象中的文本内容的 JSON 键数组
 * jsonMetadataGenerator : 可选的 JsonMetadataGenerator ，用于为每个 Document 创建元数据
 * <p>
 * 默认行为:
 * 它可以处理 JSON 数组和单个 JSON 对象
 * 如果没有指定键，它将整个 JSON 对象作为内容使用
 * 它使用提供的 JsonMetadataGenerator （如果没有提供，则使用一个空对象）生成元数据
 * 它创建一个 Document 对象，其中包含提取的内容和元数据。
 * 
 * @author bootystar
 */
@SpringBootTest
public class Rag21Reader01Json {
    
    @Value("classpath:rag/bike.json")
    private Resource resource;

    /**
     * 读取json
     */
    @Test
    void readJson() {
        JsonReader jsonReader = new JsonReader(resource);
        var documents = jsonReader.read();
        documents.forEach(System.out::println);
    }

    /**
     * 只读取json中指定key的数据
     */
    @Test
    void readJsonWithKeys() {
        JsonReader jsonReader = new JsonReader(resource, "name", "description");
        var documents = jsonReader.read();
        documents.forEach(System.out::println);
    }

    /**
     * 读取json中指定key的数据，并添加元数据
     * 元数据JsonMetadataGenerator, 本质是个Function<T,R>函数, 其中T是一个jsonMap, 代表读取到的json数据
     */
    @Test
    void readJsonWithKeysAndMetadata() {
        // 元数据
        JsonMetadataGenerator metadataGenerator = jsonMap -> {
            var author = Map.of("author", "bootystar");
            return Map.of("metadata", author);
        };
        JsonReader jsonReader = new JsonReader(
                resource,
                metadataGenerator,
                "name",
                "description"
        );
        var documents = jsonReader.read();
        documents.forEach(System.out::println);
    }

}
