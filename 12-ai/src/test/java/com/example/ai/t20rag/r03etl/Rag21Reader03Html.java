package com.example.ai.t20rag.r03etl;

import org.junit.jupiter.api.Test;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.reader.jsoup.config.JsoupDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * JsoupDocumentReader 处理 HTML 文档，使用 JSoup 库将它们转换为 Document 对象列表
 * <a href="https://jsoup.org/">jsoup官网</a>
 * <p>
 * 配置选项:
 * charset : 指定 HTML 文档的字符编码（默认为"UTF-8"）
 * selector : 一个 JSoup CSS 选择器，用于指定从哪些元素中提取文本（默认为"body"）
 * separator : 用于连接从多个选定元素中提取的文本的字符串（默认为"\n"）
 * allElements : 如果 true ，则从 <body> 元素中提取所有文本，忽略 selector （默认为 false ）
 * groupByElement : 如果 true ，为每个由 selector 匹配的元素创建一个单独的 Document （默认为 false ）
 * includeLinkUrls : 如果 true ，提取绝对链接 URL 并添加到元数据中（默认为 false ）
 * metadataTags : 要从中提取内容的 <meta> 标签名称列表（默认为 ["description", "keywords"] ）
 * additionalMetadata : 允许您向所有创建的 Document 对象添加自定义元数据
 * <p>
 * 默认规则:
 * selector 确定用于文本提取的元素
 * 如果 allElements 是 true ，则 <body> 内的所有文本将被提取到一个 Document 中
 * 如果 groupByElement 是 true ，则每个匹配 selector 的元素都会创建一个单独的 Document
 * 如果 allElements 和 groupByElement 都不是 true ，则所有匹配 selector 的元素中的文本将使用 separator 连接
 * 文档标题、来自指定 <meta> 标签的内容以及（可选的）链接 URL 将被添加到 Document 元数据中
 * 用于解析相对链接的基本 URI 将从 URL 资源中提取
 * 读取器保留选定元素中的文本内容，但会移除其中的任何 HTML 标签
 * <p>
 * 使用前需要引入依赖
 * spring-ai-jsoup-document-reader
 *
 * @author bootystar
 */
@SpringBootTest
public class Rag21Reader03Html {

    @Value("classpath:rag/index.html")
    private Resource resource;

    /**
     * 阅读html
     * <p>
     * 支持的写法:{@link org.springframework.core.io.DefaultResourceLoader#getResource(String)}
     */
    @Test
    public void readHtml() {
        var jsoupDocumentReader = new JsoupDocumentReader("classpath:rag/index.html");
//        var jsoupDocumentReader = new JsoupDocumentReader("https://www.baidu.com");
        var documents = jsoupDocumentReader.read();
        documents.forEach(System.out::println);
    }


    /**
     * 使用自定义阅读html
     */
    @Test
    public void readHtmlWithConfig() {
        JsoupDocumentReaderConfig config = JsoupDocumentReaderConfig.builder()
                .selector("article p") // 只提取文本<article>和<p>标签的内容
                .charset("UTF-8")  // 本地文件编码为UTF-8, 实际html文件编码一般为ISO-8859-1
                .includeLinkUrls(true) // 提取url的元数据, url会被储存到元数据中的linkUrls=[]中
                .metadataTags(List.of("author", "date")) // 指定从<meta>标签中提取的元数据
                .additionalMetadata("source", "index.html") // 添加自定义元数据
                .build();
        JsoupDocumentReader reader = new JsoupDocumentReader(resource, config);
        var documents = reader.read();
        documents.forEach(System.out::println);
    }


}
