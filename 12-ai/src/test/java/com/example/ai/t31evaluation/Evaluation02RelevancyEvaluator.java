package com.example.ai.t31evaluation;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * RelevancyEvaluator 是 Evaluator 接口的一种实现，旨在评估 AI 生成的响应与提供的上下文的相关性。
 * 此评估器通过确定 AI 模型的响应是否与用户对检索到的上下文的输入相关，帮助评估 RAG 流的质量
 * @see org.springframework.ai.chat.evaluation.RelevancyEvaluator
 * @see org.springframework.ai.chat.evaluation.FactCheckingEvaluator
 * @author bootystar
 */
@SpringBootTest
public class Evaluation02RelevancyEvaluator {
    
    @Autowired
    private ChatModel chatModel;
    @Autowired
    private VectorStore vectorStore;
    
    

    /**
     * 默认模板:
     */
    @Test
    void evaluateRelevancy() {
        String question = "spring ai rocks or not?,just return true or false";
        RetrievalAugmentationAdvisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .build())
                .build();
        ChatResponse chatResponse = ChatClient.builder(chatModel).build()
                .prompt(question)
                .advisors(ragAdvisor)
                .call()
                .chatResponse();
        System.out.println(chatResponse.getResult().getOutput().getText());
        EvaluationRequest evaluationRequest = new EvaluationRequest(
                // 原始问题
                question,
                // Rag上下文
                chatResponse.getMetadata().get(RetrievalAugmentationAdvisor.DOCUMENT_CONTEXT),
                // ai响应
                chatResponse.getResult().getOutput().getText()       
        );
        RelevancyEvaluator evaluator = new RelevancyEvaluator(ChatClient.builder(chatModel));
        EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);
        var pass = evaluationResponse.isPass();
        System.out.println(pass);
    }
    
    @Test
    void evaluateRelevancy2() {
        String template = """
                Your task is to evaluate if the response for the query
                is in line with the context information provided.
                
                You have two options to answer. Either YES or NO.
                
                Answer YES, if the response for the query
                is in line with context information otherwise NO.
                
                Query:
                {query}
                
                Response:
                {response}
                
                Context:
                {context}
                
                Answer:""";
        // 使用自定义模板
        var evaluator = RelevancyEvaluator.builder()
                .promptTemplate(
                        PromptTemplate.builder()
                                .template(template)
                                .build()
                )
                .build();

    }

    
}
