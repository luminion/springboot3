package com.example.ai.t31evaluation;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

/**
 * RelevancyEvaluator 是 Evaluator 接口的一种实现，旨在评估 AI 生成的响应与提供的上下文的相关性。
 * 此评估器通过确定 AI 模型的响应是否与用户对检索到的上下文的输入相关，帮助评估 RAG 流的质量
 * 
 * @see org.springframework.ai.chat.evaluation.RelevancyEvaluator
 * @see org.springframework.ai.chat.evaluation.FactCheckingEvaluator
 * @author bootystar
 */
@SpringBootTest
public class Evaluation03FactCheckingEvaluator {
    
    @Autowired
    private ChatModel chatModel;
    @Autowired
    private VectorStore pgVectorStore;


    @Test
    void testFactChecking() {
        // Example context and claim
        String context = "The Earth is the third planet from the Sun and the only astronomical object known to harbor life.";
        String claim = "The Earth is the fourth planet from the Sun.";

        // Create an EvaluationRequest
        EvaluationRequest evaluationRequest = new EvaluationRequest(
                context, 
                Collections.emptyList(), 
                claim
        );

        // Create the FactCheckingEvaluator
        var factCheckingEvaluator = new FactCheckingEvaluator(ChatClient.builder(chatModel));
        
        // Perform the evaluation
        EvaluationResponse evaluationResponse = factCheckingEvaluator.evaluate(evaluationRequest);

        var pass = evaluationResponse.isPass();
        System.out.println(pass);

    }
    
}
