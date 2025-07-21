package com.example.ai.t20rag.r03etl;

import org.junit.jupiter.api.Test;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

/**
 * PagePdfDocumentReader 使用 Apache PdfBox 库解析 PDF 文档
 * <a href="https://pdfbox.apache.org/">PdfBox官网</a>
 * ParagraphPdfDocumentReader 使用 PDF 目录（例如目录）信息将输入的 PDF 分割为文本段落，并为每个段落输出一个 Document 。
 * 注意：并非所有 PDF 文档都包含 PDF 目录
 * <p>
 * 使用前需要引入依赖
 * spring-ai-pdf-document-reader
 *
 * @author bootystar
 */
@SpringBootTest
public class Rag21Reader05Pdf {

    @Value("classpath:rag/alibaba_java.pdf")
    private Resource resource;

    /**
     * 阅读pdf
     */
    @Test
    public void readPdf() {
        var reader = new PagePdfDocumentReader("classpath:rag/alibaba_java.pdf");
        reader.read().forEach(System.out::println);

    }


    /**
     * 使用配置阅读pdf
     */
    @Test
    public void readPdfWithConfig() {
        ParagraphPdfDocumentReader pdfReader = new ParagraphPdfDocumentReader(
                resource,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());
        pdfReader.read().forEach(System.out::println);
    }
    



}
