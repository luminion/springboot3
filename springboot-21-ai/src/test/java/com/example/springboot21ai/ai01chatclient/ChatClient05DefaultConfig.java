package com.example.springboot21ai.ai01chatclient;

import com.example.springboot21ai.ai02prompt.Prompt01TemplateEngine;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 可以创建具有默认系统文本的 ChatClient 可以简化运行时代码。
 * 通过设置默认值，您只需在调用 ChatClient 时指定用户文本，无需为运行时代码路径中的每个请求设置系统文本
 * <p>
 * Other defaults  其他默认值
 * 在 ChatClient.Builder 级别，您可以指定默认提示配置
 * <p>
 * defaultOptions(ChatOptions chatOptions)
 * 传入 ChatOptions 类中定义的可移植选项或特定于模型的选项，例如 OpenAiChatOptions 中的选项。
 * 有关特定于模型的 ChatOptions 实现的更多信息，请参阅 JavaDocs
 * <p>
 * defaultFunction(String name, String description, java.util.function.Function<I, O> function)
 * 该名称用于在用户文本中引用函数。
 * 该描述解释了函数的用途，并帮助 AI 模型选择正确的函数以获得准确的响应。function 参数是模型将在必要时执行的 Java 函数实例
 * <p>
 * defaultFunctions(String... functionNames)
 * 在应用程序上下文中定义的 'java.util.Function' 的 bean 名称
 * <p>
 * defaultUser（String text）
 * defaultUser（Resource text）
 * defaultUser(Consumer<UserSpec> userSpecConsumer)
 * 这些方法允许您定义用户文本。
 * Consumer<UserSpec> 允许您使用 lambda 指定用户文本和任何默认参数
 * <p>
 * defaultAdvisors(Advisor… advisor) ：
 * 顾问程序允许修改用于创建提示的数据。
 * QuestionAnswerAdvisor 实现通过在提示后附加与用户文本相关的上下文信息 Retrieval Augmented Generation 来启用模式
 * <p>
 * defaultAdvisors(Consumer<AdvisorSpec> advisorSpecConsumer) ：
 * 此方法允许您定义一个 Consumer 以使用 AdvisorSpec 配置多个 advisor。
 * 顾问可以修改用于创建最终 Prompt 的数据。
 * Consumer<AdvisorSpec> 允许您指定一个 lambda 来添加顾问程序，
 * 例如 QuestionAnswerAdvisor，它支持 Retrieval Augmented Generation 根据用户文本在提示中附加相关上下文信息
 * <p>
 * 您可以在运行时使用不带 default 前缀的相应方法覆盖这些默认值
 * options(ChatOptions chatOptions)
 * function(String name, String description, java.util.function.Function<I, O> function)
 * functions(String… functionNames)
 * user(String text), user(Resource text), user(Consumer<UserSpec> userSpecConsumer)
 * advisors(Advisor… advisor)
 * advisors(Consumer<AdvisorSpec> advisorSpecConsumer)
 *
 * @author bootystar
 */
@SpringBootTest
public class ChatClient05DefaultConfig {

    @Autowired
    @Qualifier("baseChatModel")
    private OpenAiChatModel chatModel;

    /**
     * 配置缺省的默认系统角色信息
     */
    @Test
    void promptDefaultSystem() {
        // 此处的chatClient在AiClientConfig中配置了默认的system prompt, 所以其会以中文回答
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        var chatClient = builder
                .defaultSystem("""
                        你是一个以中文为主的模型
                        即使用户输入英文, 所有回答也均采用中文回答
                        并尽量产出中文为主的内容
                        """
                ) // 默认的system prompt, 当未输入system prompt时采用
                .build();
        var content = chatClient.prompt()
                .user("tell me about who can win tour de france of this year ")
                .call().content();
        System.out.println(content);
    }


    /**
     * 设置默认的系统prompt,并设置占位符变量
     *
     * @see Prompt01TemplateEngine 自定义模板
     */
    @Test
    void promptTemplateWithDefaultSystem() {
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        var chatClient = builder
                .defaultSystem("""
                        你是一个老师, 你得名字是{name}
                        即使用户输入英文, 所有回答也均采用中文回答
                        并尽量产出中文为主的内容
                        """
                ) // 默认的prompt, 当未输入prompt时采用
                .build();
        var content = chatClient.prompt()
                .system(e -> e.param("name", "张三")) // 设置系统prompt提示词的占位符
                .user("tell me who you are")
                .call().content();
        System.out.println(content);
    }


}
