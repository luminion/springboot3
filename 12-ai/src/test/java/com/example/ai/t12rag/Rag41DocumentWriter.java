package com.example.ai.t12rag;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.writer.FileDocumentWriter;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * FileDocumentWriter 是一个 DocumentWriter 实现，它将一个 Document 对象列表的内容写入文件
 * 
 * 构造函数：
 * FileDocumentWriter(String fileName)
 * FileDocumentWriter(String fileName, boolean withDocumentMarkers)
 * FileDocumentWriter(String fileName, boolean withDocumentMarkers, MetadataMode metadataMode, boolean append)
 * fileName : 写入文档的文件名。
 * withDocumentMarkers : 是否在输出中包含文档标记（默认：false）。
 * metadataMode : 指定要写入文件的内容（默认：MetadataMode.NONE）。
 * append : 如果为 true，数据将被写入文件的末尾而不是开头（默认：false）。
 * 
 * 默认行为:
 * 它为指定的文件名打开一个 FileWriter。
 * 对于输入列表中的每个文档：
 * ---如果 withDocumentMarkers 为真，它写入一个包含文档索引和页码的文档标记。
 * ---它根据指定的 metadataMode 写入文档的格式化内容。
 * 文件在所有文档写入完成后关闭。
 * 
 * Document Markers  文档标记
 * 当 withDocumentMarkers 设置为 true 时，写入器为每个文档包含以下格式的标记：
 * ### Doc: [index], pages:[start_page_number,end_page_number]
 * 
 * Metadata Handling  元数据管理,作者使用了两个特定的元数据键：
 * page_number : 代表文档的起始页码
 * end_page_number : 代表文档的结束页码
 * 
 * 该处理器使用 FileWriter ，因此它使用操作系统的默认字符编码写入文本文件
 * metadataMode 参数允许控制现有元数据如何被整合到写入内容中
 * 这个处理器特别适用于调试或创建文档集合的可读输出
 * 
 * @author bootystar
 */
@SpringBootTest
public class Rag41DocumentWriter {
    
    
    @Test
    public void test(){
        FileDocumentWriter writer = new FileDocumentWriter("output.txt", true, MetadataMode.ALL, false);

        Document doc1 = new Document("java AI wonderful!! java AI wonderful!! java AI wonderful!! java AI wonderful!! java AI wonderful!!");
        Document doc2 = new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!");
        writer.write(List.of(doc1, doc2));
        // 这将把所有文档写入"output.txt"文件，包括文档标记，使用所有可用元数据，如果文件已存在，则追加写入。
        
    }
    
}
