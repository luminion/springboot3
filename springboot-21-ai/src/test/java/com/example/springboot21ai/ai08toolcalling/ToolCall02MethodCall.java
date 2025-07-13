package com.example.springboot21ai.ai08toolcalling;

import com.example.springboot21ai.entity.ActorsFilms;
import com.example.springboot21ai.tool.CustomTools;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.execution.DefaultToolCallResultConverter;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 将方法作为工具
 * <p>
 * Spring AI 提供了两种方式来指定方法作为工具
 * <p>
 * 1.通过注解{@link Tool} 指定方法, 并结合{@link ToolParam}描述该方法的作用及参数
 * <p>
 * 2.创建{@link ToolCallback}的实现
 *
 * @author bootystar
 * @see CustomTools 根据注解创建的工具
 */
@SpringBootTest
public class ToolCall02MethodCall {

    @Autowired
    @Qualifier("qwen_plus")
    private ChatModel chatModel;


    /**
     * 创建指定方法的MethodToolCallback
     * <p>
     * 方法:{@link CustomTools#getCurrentDateTime()}
     */
    @Test
    @SneakyThrows
    void createToolCallback() {
        // 获取方法
        Method method = ReflectionUtils.findMethod(CustomTools.class, "getCurrentDateTime");

        // ToolDefinition 实例，并定义工具名称、描述和输入模式
        ToolDefinition toolDefinition = ToolDefinition.builder()
                .name("getCurrentDateTime")
                .description("Get the current date and time in the user's timezone")
                .inputSchema("null") // 输入参数描述, 支持JSON String, Number, Array, Object or token 'null', 'true' or 'false'
                .build();

        // 定义额外设置的 ToolMetadata 实例，例如结果是否应直接返回给客户端，以及要使用的转换器。
        ToolMetadata toolMetadata = ToolMetadata.builder()
                .build();

        // 构建一个 MethodToolCallback 实例，并提供有关该工具的关键信息
        ToolCallback toolCallback = MethodToolCallback.builder()
                .toolDefinition(toolDefinition)
                .toolMetadata(toolMetadata)
                .toolMethod(method) // 代表工具方法的 Method 实例。必填
                .toolObject(new CustomTools()) // 包含工具方法的对象实例。如果方法是静态的，可以省略此参数
                .toolCallResultConverter(new DefaultToolCallResultConverter()) //用于将工具调用的结果转换为发送回 AI 模型的 String 对象的 ToolCallResultConverter 实例。如果未提供，将使用默认转换器（ DefaultToolCallResultConverter ）
                .build();

        // 将ToolCallback传递给ChatOptions
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(toolCallback)
                .build();
        Prompt prompt = new Prompt("What day is tomorrow?", chatOptions);
        var chatClient = ChatClient.builder(chatModel)
                .defaultOptions(chatOptions)
                .build();

        var content = chatClient.prompt(prompt)
                .call()
                .content();
        System.out.println(content);
    }


    /**
     * 调用参数类型为实体类的工具
     * <p>
     * 注意,参数需要加上注解说明
     * 方法:{@link CustomTools#saveActorsFilmsToDatabase(ActorsFilms)}
     */
    @Test
    void toolCallbackWithEntityParams() {
        var resp = ChatClient.create(chatModel)
                .prompt()
                .user("Generate the filmography of 5 movies for Tom Hanks and Bill Murray and save it to database.")
                .tools(new CustomTools())
                .call()
                .content();
        System.out.println(resp);
    }


}
