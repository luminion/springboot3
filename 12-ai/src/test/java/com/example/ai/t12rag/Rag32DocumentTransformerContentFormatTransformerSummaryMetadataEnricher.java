package com.example.ai.t12rag;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.model.transformer.SummaryMetadataEnricher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * SummaryMetadataEnricher 是一个使用生成式 AI 模型为文档创建摘要并将其作为元数据添加的 DocumentTransformer 。它可以生成当前文档的摘要，以及相邻文档（前一个和后一个）
 * <p>
 * SummaryMetadataEnricher 提供了两个构造函数:
 * SummaryMetadataEnricher(ChatModel chatModel, List<SummaryType> summaryTypes)
 * SummaryMetadataEnricher(ChatModel chatModel, List<SummaryType> summaryTypes, String summaryTemplate, MetadataMode metadataMode)
 * chatModel : 用于生成摘要的 AI 模型
 * summaryTypes : 一个 SummaryType 枚举值列表，指示要生成哪些摘要（PREVIOUS, CURRENT, NEXT）
 * summaryTemplate : 用于摘要生成的自定义模板（可选）
 * metadataMode : 指定在生成摘要时如何处理文档元数据（可选）
 * 
 * 默认行为:
 * 对于每个输入文档，它使用文档内容和指定的摘要模板创建一个提示。
 * 它将这个提示发送到提供的 ChatModel 以生成摘要
 * 根据指定的 summaryTypes ，它为每个文档添加以下元数据：
 * ---section_summary ：当前文档的摘要
 * ---prev_section_summary ：前一个文档的摘要（如果可用且请求）。
 * ---next_section_summary : 下一个文档的摘要（如果可用且已请求）。
 * 富集后的文档被返回。
 * <p>
 * 摘要生成提示可以通过提供自定义的 summaryTemplate 进行定制。默认模板是
 * 
 * 注意:
 * SummaryMetadataEnricher 需要一个功能正常的 ChatModel 来生成摘要
 * 增强器可以处理任意大小的文档列表，正确处理第一个和最后一个文档的边缘情况。
 * 这种增强器特别适用于创建上下文感知的摘要，允许更好地理解序列中文档之间的关系
 * MetadataMode 参数允许控制现有元数据如何被整合到摘要生成过程中
 *
 * @author bootystar
 */
@SpringBootTest
public class Rag32DocumentTransformerContentFormatTransformerSummaryMetadataEnricher {

    @Autowired
    private ChatModel chatModel;
    
    /*
    摘要生成提示可以通过提供自定义的 summaryTemplate 进行定制。默认模板是
    """
    Here is the content of the section:
    {context_str}
    
    Summarize the key topics and entities of the section.
    
    Summary:
    """
     */

    /**
     * 对于一份包含个文档的列表，两个文档都将获得 section_summary
     * 一个文档接收了 next_section_summary 但没有 prev_section_summary 
     * 第二个文档接收 prev_section_summary 但没有 next_section_summary
     * 第一个文档的 section_summary 与第二个文档的 prev_section_summary 匹配
     * 第一个文档的 next_section_summary 与第二个文档的 section_summary 匹配
     */
    @Test
    public void test() {
        var enricher  = new SummaryMetadataEnricher(chatModel,
                List.of(
                        SummaryMetadataEnricher.SummaryType.PREVIOUS,
                        SummaryMetadataEnricher.SummaryType.CURRENT,
                        SummaryMetadataEnricher.SummaryType.NEXT
                )
        );

        Document doc1 = new Document("java AI wonderful!! java AI wonderful!! java AI wonderful!! java AI wonderful!! java AI wonderful!!");
        Document doc2 = new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!");

        List<Document> enrichedDocs = enricher.apply(List.of(doc1, doc2));
        for (Document doc : enrichedDocs) {
            System.out.println("Current summary: " + doc.getMetadata().get("section_summary"));
            System.out.println("Previous summary: " + doc.getMetadata().get("prev_section_summary"));
            System.out.println("Next summary: " + doc.getMetadata().get("next_section_summary"));
        }
    }

}
