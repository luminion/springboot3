package com.example.ai.t12rag;

import org.junit.jupiter.api.Test;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

/**
 * TikaDocumentReader 使用 Apache Tika 从多种文档格式中提取文本，例如 PDF、DOC/DOCX、PPT/PPTX 和 HTML。有关支持格式的完整列表，请参阅 Tika 文档
 * <a href="https://tika.apache.org/">Tika官网</a>
 * <p>
 * 使用前需要引入依赖
 * spring-ai-tika-document-reader
 *
 * @author bootystar
 */
@SpringBootTest
public class Rag26DocumentReaderTika {
    
    @Value("classpath:rag/simple.docx")
    private Resource resource;

    @Test
    public void test() {
        var reader = new TikaDocumentReader(resource);
        var documents = reader.read();
        documents.forEach(System.out::println);
    }


}
