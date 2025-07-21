package com.example.ai.t31evaluation;

import org.springframework.boot.test.context.SpringBootTest;

/**
 * Evaluator用于评估ai生成的内容, 以确保 AI 模型没有产生幻觉响应。
 * 评估的方式为将用户输入, 上下文, ai响应作为输入, 交给ai模型进行评估,
 * 这里用于评估的ai模型最好不要使用和用于生成响应的模型相同, 以避免产生幻觉响应
 * <p>
 * 评估的输入是 EvaluationRequest，有3个属性:
 * userText：用户的原始输入（ 字符串 ）
 * dataList：附加到原始输入的上下文数据，例如来自 Retrieval Augmented Generation 的数据。
 * responseContent：AI 模型的响应内容（ 以 String 形式）
 *
 * @author bootystar
 * @see org.springframework.ai.evaluation.Evaluator
 * @see org.springframework.ai.chat.evaluation.RelevancyEvaluator
 * @see org.springframework.ai.chat.evaluation.FactCheckingEvaluator
 */
@SpringBootTest
public class Evaluation01 {



}
