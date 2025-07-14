package com.example.springboot21ai.ai10toolcalling;

import com.example.springboot21ai.tool.DateTimeTools;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
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
 * 工具的定义
 * {@link ToolCallback}
 * ToolCallback 接口提供了一种定义可由 AI 模型调用的工具的方法，包括定义和执行逻辑。
 * <p>
 * 当您想从头开始定义一个工具时，它是要实现的主界面。
 * <p>
 * 例如，您可以从 MCP 客户端（使用模型上下文协议）或 ChatClient（以构建模块化代理应用程序）定义 ToolCallback
 * 工具调用功能的所有主要作都记录在 DEBUG 级别。您可以通过将 org.springframework.ai 包的日志级别设置为 DEBUG 来启用日志记录
 *
 * @author bootystar
 */
@SpringBootTest
public class ToolCall03ToolCallBack {

    @Autowired
    @Qualifier("qwen_plus")
    private ChatModel chatModel;

    /**
     * 创建指定方法的MethodToolCallback
     * <p>
     * 方法:{@link DateTimeTools#getCurrentDateTime()}
     */
    @Test
    @SneakyThrows
    void createToolCallback() {
        // 获取方法
        Method method = ReflectionUtils.findMethod(DateTimeTools.class, "getCurrentDateTime");

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
                .toolObject(new DateTimeTools()) // 包含工具方法的对象实例。如果方法是静态的，可以省略此参数
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

}
