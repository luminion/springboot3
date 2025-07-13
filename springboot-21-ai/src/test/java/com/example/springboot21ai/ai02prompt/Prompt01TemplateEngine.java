package com.example.springboot21ai.ai02prompt;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

/**
 * <a href="https://docs.spring.io/spring-ai/reference/api/prompt.html">Spring AI Prompt文档</a>
 * <p>
 * Prompt 类充当一系列有组织的 Message 对象和请求 ChatOptions 的容器。
 * 每条 Message 在提示中都包含一个独特的角色，其内容和意图不同。
 * 这些角色可以包含各种元素，从用户查询到 AI 生成的对相关背景信息的响应。
 * <p>
 * System Role:
 * 指导 AI 的行为和响应风格，为 AI 如何解释和回复输入设置参数或规则。这类似于在开始对话之前向 AI 提供指令
 * <p>
 * User Role:
 * 表示用户的输入 – 他们对 AI 的问题、命令或陈述。这个角色是基础，因为它构成了 AI 响应的基础。
 * <p>
 * Assistant Role:
 * AI对用户输入的响应。它不仅仅是一个答案或反应，它对于保持对话的流畅性也至关重要。通过跟踪 AI 之前的响应（其“助理角色”消息），系统可确保连贯且与上下文相关的交互。
 * Assistant消息也可能包含函数工具调用请求信息。它就像 AI 中的一项特殊功能，在需要时用于执行特定功能，例如计算、获取数据或除交谈之外的其他任务
 * <p>
 * Tool/Function Role:
 * 工具/功能角色 侧重于返回其他信息以响应工具调用助手消息
 *
 * @author bootystar
 */
@SpringBootTest
public class Prompt01TemplateEngine {

    @Autowired
    @Qualifier("baseChatModel")
    private OpenAiChatModel chatModel;

    /**
     * Spring AI 中提示模板的一个关键组件是 PromptTemplate 类，该类旨在促进创建结构化提示，然后将其发送到 AI 模型进行处理
     * Spring AI 中提示模板的一个关键组件是 PromptTemplate 类，该类旨在促进创建结构化提示，然后将其发送到 AI 模型进行处理
     * 此类使用 TemplateRenderer API 来渲染模板。
     * 默认情况下，Spring AI 使用 StTemplateRenderer 实现，该实现基于 Terence Parr 开发的开源 StringTemplate 引擎。
     * 模板变量由 {} 语法标识，但您也可以将分隔符配置为使用其他语法
     */
    @Test
    void testTemplateRenderer() {
        // 使用<>替换默认的{}
        PromptTemplate promptTemplate = PromptTemplate.builder()
                .renderer(
                        StTemplateRenderer.builder()
                                .startDelimiterToken('<')
                                .endDelimiterToken('>')
                                .build()
                )
                .template("""
                        Tell me the names of 5 movies whose soundtrack was composed by <composer>.
                        """)
                .build();

        String prompt = promptTemplate.render(Map.of("composer", "John Williams"));
        var content = ChatClient.create(chatModel).prompt()
                .user(prompt)
                .call()
                .content();
        System.out.println(content);
    }

    /**
     * 使用 SystemPromptTemplate 创建一个 Message 来构建 Prompt 实例，其中系统角色传入占位符值。
     * 然后，带有角色 user 的消息将与角色系统的消息组合在一起以形成提示。然后将提示传递给 ChatModel 以获得生成响应
     */
    @Test
    void testSystemPromptTemplate() {
        String userText = """
                Tell me about three famous pirates from the Golden Age of Piracy and why they did.
                Write at least a sentence for each pirate.
                """;

        Message userMessage = new UserMessage(userText);

        String systemText = """
                You are a helpful AI assistant that helps people find information.
                Your name is {name}
                You should reply to the user's request with your name and also in the style of a {voice}.
                You should reply with chinese
                """;
        
        String name = "Mr.bean";
        String voice = "cartoon";
        
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", name, "voice", voice));

        Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

        var content = ChatClient.create(chatModel).prompt(prompt).call().content();
        System.out.println(content);
    }


//    @Value("classpath:/prompts/system_message.st") // 注入资源,指定资源路径
//    private org.springframework.core.io.Resource systemResource;

    /**
     * Spring AI 支持抽象 org.springframework.core.io.Resource ，因此您可以将提示数据放在可以直接在 PromptTemplate 中使用的文件中。
     * 例如，您可以在 Spring 托管组件中定义一个字段来检索 Resource, 然后将该资源直接传递给 SystemPromptTemplate
     */
    @Test
    void testSystemResource() {
        // 除了注入org.springframework.core.io.Resource,还可以指定具体实现类和路径自行创建
        var systemResource = new org.springframework.core.io.ClassPathResource("/prompts/system_message.st");
        // 读取文件,获取用于信息
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemResource);
        String name = "海绵宝宝";
        String voice = "卖萌";
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", name, "voice", voice));

        String userText = """
                Tell me about three famous pirates from the Golden Age of Piracy and why they did.
                Write at least a sentence for each pirate.
                """;
        Message userMessage = new UserMessage(userText);
        
        Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

        var content = ChatClient.create(chatModel).prompt(prompt).call().content();
        System.out.println(content);
    }


    /**
     * 通过builder创建Prompt
     */
    @Test
    void testPromptTemplateBuilder() {
        String template = """
                Generate 3 filmography for {actor}.
                {format}
                """;
        String actor = "Tom Hanks";
        String format = "return with json";
        var prompt = PromptTemplate.builder()
                .template(template)
                .variables(Map.of("actor", actor, "format", format))
                .build()
                .create();
        var content = ChatClient.create(chatModel).prompt(prompt).call().content();
        System.out.println(content);
    }
}
