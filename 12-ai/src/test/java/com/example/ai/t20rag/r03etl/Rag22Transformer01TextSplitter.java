package com.example.ai.t20rag.r03etl;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

/**
 * TextSplitter 是一个抽象基类，用于帮助将文档分割以适应 AI 模型的上下文窗口
 * <p>
 * TokenTextSplitter 是一个基于 TextSplitter 的实现，它根据 token 数量将文本分割成多个片段，使用 CL100K_BASE 编码
 * <p>
 * TokenTextSplitter 提供两种构造选项:
 * <p>
 * TokenTextSplitter()
 * 创建具有默认设置的分割器
 * <p>
 * TokenTextSplitter(int defaultChunkSize, int minChunkSizeChars, int minChunkLengthToEmbed, int maxNumChunks, boolean keepSeparator):
 * defaultChunkSize : 每个文本块的目标大小（以令牌计）（默认：800）
 * minChunkSizeChars : 每个文本块的最小大小（以字符计）（默认：350）
 * minChunkLengthToEmbed : 包含块的最小长度（默认：5）
 * maxNumChunks : 从文本中生成的最大块数（默认：10000）
 * keepSeparator : 是否在块中保留分隔符（如换行符）（默认：true）
 * <p>
 * 默认行为:
 * 1.它使用 CL100K_BASE 编码将输入文本编码为标记
 * 2.它根据 defaultChunkSize 将编码的文本分割成块
 * 3.对于每个块:
 * 3.1.它将数据块解码回文本
 * 3.2.它尝试在 minChunkSizeChars 之后找到一个合适的断点（句号、问号、感叹号或换行符）
 * 3.3.如果发现断点，它会在该点截断块
 * 3.4.裁剪块，并根据 keepSeparator 设置选择性地删除换行符
 * 3.5.如果生成的块长度大于 minChunkLengthToEmbed ，则将其添加到输出中
 * 4.过程3.1-3.5持续进行，直到所有标记被处理或达到 maxNumChunks
 * 5.如果剩余文本长度大于 minChunkLengthToEmbed ，则将其作为最终块添加
 * <p>
 * 注释:
 * TokenTextSplitter 使用 jtokkit 库中的 CL100K_BASE 编码，该编码与较新的 OpenAI 模型兼容
 * 分词器尝试在可能的情况下，在句子边界处分割，以创建语义上有意义的片段。
 * 原始文档的元数据被保留并复制到所有从该文档派生出来的片段中。
 * 原始文档中的内容格式化器（如果已设置）也会被复制到派生片段中，如果 copyContentFormatter 被设置为 true （默认行为）。
 * 这种分割器特别适用于为大语言模型准备文本，这些模型有令牌限制，确保每个片段都在模型的处理能力范围内。
 *
 * @author bootystar
 */
@SpringBootTest
public class Rag22Transformer01TextSplitter {

    @Value("classpath:rag/alibaba_java.pdf")
    private Resource resource;
    
    /**
     * 拆分文本
     */
    @Test
    public void tokenTextSplitter() {
        Document doc1 = new Document("This is a long piece of text that needs to be split into smaller chunks for processing.",
                Map.of("source", "example.txt"));
        Document doc2 = new Document("Another document with content that will be split based on token count.",
                Map.of("source", "example2.txt"));
        
//        TokenTextSplitter splitter = new TokenTextSplitter();
        TokenTextSplitter splitter = new TokenTextSplitter(10000, 300, 10, 5000, true);
        List<Document> splitDocuments = splitter.apply(List.of(doc1, doc2));

        for (Document doc : splitDocuments) {
            System.out.println("Chunk: " + doc.getFormattedContent());
            System.out.println("Metadata: " + doc.getMetadata());
        }
    }

}
