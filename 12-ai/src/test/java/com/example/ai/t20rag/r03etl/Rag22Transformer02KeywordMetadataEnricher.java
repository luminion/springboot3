package com.example.ai.t20rag.r03etl;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * ContentFormatTransformer 内容格式转换器
 * 确保所有文档的内容格式一致。
 * <p>
 * KeywordMetadataEnricher 是一个 DocumentTransformer ，它使用生成式 AI 模型从文档内容中提取关键词，并将其作为元数据添加
 * KeywordMetadataEnricher 构造函数接受两个参数：
 * ChatModel chatModel : 用于生成关键词的 AI 模型。
 * int keywordCount : 每个文档提取的关键词数量。
 * <p>
 * 默认行为:
 * 对于每个输入文档，它使用文档内容创建一个提示。
 * 它将此提示发送到提供的 ChatModel 以生成关键词。
 * 生成的关键词被添加到文档的元数据中，键名为 "excerpt_keywords"。
 * 富集后的文档被返回。
 * <p>
 * 注意:
 * KeywordMetadataEnricher 需要一个功能正常的 ChatModel 来生成关键词
 * 关键词数量必须为 1 或更多
 * 增强器为每个处理的文档添加"excerpt_keywords"元数据字段
 * 生成的关键词以逗号分隔的字符串形式返回
 * 这种增强器特别适用于提高文档的可搜索性，以及为文档生成标签或类别
 *
 * @author bootystar
 */
@SpringBootTest
public class Rag22Transformer02KeywordMetadataEnricher {

    @Autowired
    private ChatModel chatModel;

    @Test
    public void test() {
        KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(chatModel, 5);

        Document doc = new Document("This is a document about artificial intelligence and its applications in modern technology.");

        List<Document> enrichedDocs = enricher.apply(List.of(doc));

        Document enrichedDoc = enrichedDocs.get(0);
        String keywords = (String) enrichedDoc.getMetadata().get("excerpt_keywords");
        System.out.println("Extracted keywords: " + keywords);
        /*
        Extracted keywords: artificial intelligence, modern technology, applications, machine learning, automation
         */
    }

}
