package com.example.ai.t20rag.r04advisor;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * QuestionAnswerAdvisor
 * 向量数据库存储 AI 模型不知道的数据。当用户问题发送到 AI 模型时，
 * QuestionAnswerAdvisor 会查询向量数据库以获取与用户问题相关的文档
 * 向量数据库的响应会附加到用户文本中，为 AI 模型生成响应提供上下文
 *
 * @author bootystar
 */
@SpringBootTest
public class Rag51QuestionAnswerAdvisor {

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private VectorStore vectorStore;

    /**
     * QuestionAnswerAdvisor 将在向量数据库中的所有文档上执行相似性搜索
     * @see org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor
     */
    @Test
    void questionAnswerAdvisor() {
        String userText = """
                tell what you know about spring
                """;
        var qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .similarityThreshold(0.6d)
                        .topK(6)
                        .build()
                )
                .build();
        var content = chatClient
                .prompt()
                .advisors(qaAdvisor)
                .user(userText)
                .call()
                .content();
        System.out.println(content);
    }

    /**
     * 动态过滤表达式
     * 使用 FILTER_EXPRESSION 顾问上下文参数在运行时更新 SearchRequest 过滤表达式
     * FILTER_EXPRESSION参数允许您根据提供的表达式动态过滤搜索结果
     * 
     */
    @Test
    void dynamicFilterExpression() {
        var questionAnswerAdvisor = new QuestionAnswerAdvisor(vectorStore);
        // 检索ai关键词
        String content = chatClient.prompt()
                .user("tell what you know about ai")
                .advisors(questionAnswerAdvisor)
                .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "author == 'bootystar'")) // 过滤元数据,取作者为bootystar的文档 
                .call()
                .content();
        System.out.println(content);
        System.out.println("=========================");
        String content1 = chatClient.prompt()
                .user("tell what you know about ai")
                .advisors(questionAnswerAdvisor)
                .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "author == 'booty'")) // 过滤元数据,取作者为booty的文档
                .call()
                .content();
        System.out.println(content1);
    }

    /**
     * 自定义模板
     * QuestionAnswerAdvisor 使用默认模板来增强用户问题以检索到的文档。您可以通过 .promptTemplate() 构建方法提供自己的 PromptTemplate 对象来自定义此行为
     * 自定义的 PromptTemplate 可以使用任何 TemplateRenderer 实现（默认情况下，它使用基于 StringTemplate 引擎的 StPromptTemplate ）。
     * 重要的要求是模板必须包含以下两个占位符
     * 一个 query 占位符来接收用户问题
     * 一个 question_answer_context 占位符来接收检索到的上下文
     * 剩余的提示词可以根据您的需要进行自定义, 例如使用什么语气回答等
     */
    @Test
    void customTemplate() {
        PromptTemplate customPromptTemplate = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template("""
                <query>
    
                Context information is below.
                ---------------------
                <question_answer_context>
                ---------------------
    
                Given the context information and no prior knowledge, answer the query.
    
                Follow these rules:
    
                1. If the answer is not in the context, just say that you don't know.
                2. Avoid statements like "Based on the context..." or "The provided information...".
                """)
                .build();

        String question = "Where does the adventure of Anacletus and Birba take place?";

        QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .promptTemplate(customPromptTemplate)
                .build();

        String response = chatClient
                .prompt(question)
                .advisors(qaAdvisor)
                .call()
                .content();
        System.out.println(response);
    }
}
