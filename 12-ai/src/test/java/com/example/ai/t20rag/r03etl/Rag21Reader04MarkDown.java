package com.example.ai.t20rag.r03etl;

import org.junit.jupiter.api.Test;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

/**
 * MarkdownDocumentReader 处理 Markdown 文档，将它们转换为 Document 对象列表
 * <p>
 * 配置选项:
 * horizontalRuleCreateDocument : 当设置为 true 时，Markdown 中的水平规则将创建新的 Document 对象
 * includeCodeBlock : 当设置为 true 时，代码块将与周围的文本包含在同一个 Document 中。当 false 时，代码块创建独立的 Document 对象
 * includeBlockquote : 当设置为 true 时，引用块将与周围的文本包含在同一个 Document 中。当 false 时，引用块创建独立的 Document 对象
 * additionalMetadata : 允许您为所有创建的 Document 对象添加自定义元数据
 * <p>
 * 默认规则:
 * 标题成为 Document 对象中的元数据。
 * 段落成为 Document 对象的内容。
 * 代码块可以单独成为 Document 对象，或包含在周围的文本中。
 * 引用块可以单独成为 Document 对象，或包含在周围的文本中。
 * 水平线可用于将内容分割成单独的 Document 对象。
 *
 * 使用前需要引入依赖
 * spring-ai-markdown-document-reader
 *
 * @author bootystar
 */
@SpringBootTest
public class Rag21Reader04MarkDown {

    @Value("classpath:rag/spring.md")
    private Resource resource;
    
    /**
     * 读取markdown文件
     * <p>
     * 支持的写法:{@link org.springframework.core.io.DefaultResourceLoader#getResource(String)}
     */
    @Test
    public void readMarkDown() {
        MarkdownDocumentReader reader = new MarkdownDocumentReader("classpath:rag/spring.md");
        reader.read().forEach(System.out::println);
    }


    /**
     * 根据自定义配置读取Markdown文件
     */
    @Test
    public void readMarkDownWithConfig() {
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(false)
                .withIncludeBlockquote(false)
                .withAdditionalMetadata("filename", "code.md")
                .build();
        MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
        reader.read().forEach(System.out::println);


    }


}
