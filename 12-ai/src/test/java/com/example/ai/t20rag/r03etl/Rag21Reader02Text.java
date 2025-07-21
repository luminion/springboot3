package com.example.ai.t20rag.r03etl;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * TextReader 处理纯文本文档，将它们转换为 Document 对象列表。
 * <p>
 * 配置选项:
 * setCharset(Charset charset) : 设置用于读取文本文件的字符集。默认为 UTF-8
 * getCustomMetadata() : 返回一个可变映射Map，你可以在其中为文档添加自定义元数据
 * <p>
 * 默认规则:
 * 它将整个文本文件的内容读入一个 Document 对象中。
 * 文件的内容成为 Document 的内容。
 * 元数据charset和source会自动添加到 Document中 (charset : 读取文件时使用的字符集（默认："UTF-8"）source : 源文本文件的文件名)
 * 也可以通过 getCustomMetadata() 添加的任何自定义元数据都包含在 Document 中
 * <p>
 * TextReader 将整个文件内容读入内存，因此可能不适合非常大的文件
 * 如果您需要将文本拆分为更小的块，您可以在读取文档后使用文本拆分器 TokenTextSplitter
 *
 * @author bootystar
 */
@SpringBootTest
public class Rag21Reader02Text {

    @Value("classpath:rag/mars.txt")
    private Resource resource;

    /**
     * 读取文本文件
     * <p>
     * 支持的写法:{@link org.springframework.core.io.DefaultResourceLoader#getResource(String)}
     */
    @Test
    public void readText() {
        var textReader = new TextReader("classpath:rag/mars.txt");
        textReader.setCharset(StandardCharsets.UTF_8);
        var documents = textReader.read();
        documents.forEach(System.out::println);
    }

    /**
     * 读取文本文件(使用Resource构造器)
     * 使用文本拆分器 TokenTextSplitter将文档拆分为更小的块
     */
    @Test
    public void readTextWithSplit() {
        var textReader = new TextReader(resource);
        textReader.setCharset(StandardCharsets.UTF_8);
        var documents = textReader.read();
        var tokenTextSplitter = TokenTextSplitter.builder()
                .build();
        // 拆分文本
        List<Document> splitDocuments = tokenTextSplitter.apply(documents);
        splitDocuments.forEach(System.out::println);
    }


}
