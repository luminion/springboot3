package com.example.ai.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;

import java.util.List;

/**
 * DocumentPostProcessor的简单实现
 * @author bootystar
 */
@Slf4j
public class SimpleDocumentPostProcessor implements DocumentPostProcessor {
    @Override
    public List<Document> process(Query query, List<Document> documents) {
        log.info("SimpleDocumentPostProcessor.process");
        return List.of();
    }
}
