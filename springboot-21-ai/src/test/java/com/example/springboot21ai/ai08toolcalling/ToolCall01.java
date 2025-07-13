package com.example.springboot21ai.ai08toolcalling;

import com.example.springboot21ai.entity.ActorsFilms;
import com.example.springboot21ai.tool.CustomTools;
import com.example.springboot21ai.tool.WeatherService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.execution.DefaultToolCallResultConverter;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 工具调用（也称为函数调用）是 AI 应用中的一种常见模式，允许模型与一组 API 或工具交互，增强其功能。
 * <p>
 * 1.当我们想向模型提供工具时，会在聊天请求中包含其定义。每个工具定义都由名称、描述和输入参数的架构组成
 * <p>
 * 2.当模型决定调用工具时，它会发送一个包含工具名称和按照定义的架构建模的输入参数的响应
 * <p>
 * 3.应用程序负责使用工具名称来识别并使用提供的输入参数执行该工具
 * <p>
 * 4.工具调用的结果由应用程序处理
 * <p>
 * 5.应用程序将工具调用结果发送回模型
 * <p>
 * 6.模型使用工具调用结果作为附加上下文来生成最终响应
 * <p>
 * Spring使用{@link ToolCallback}接口来抽象方法调用
 * <p>
 * 并由{@link org.springframework.ai.model.tool.ToolCallingManager}管理生命周期
 * <p>
 * 除了直接传递 ToolCallback 对象，您还可以传递一个工具名称列表，该列表将使用 ToolCallbackResolver 接口动态解析
 * <p>
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
public class ToolCall01 {

    @Autowired
    @Qualifier("qwen_plus")
    private ChatModel chatModel;

    /**
     * 通过注解{@link Tool} 指定方法, 为大模型提供获取当前时间的tool
     * <p>
     * 通过注解指定方法:{@link CustomTools#getCurrentDateTime()}
     */
    @Test
    void setTomorrowRemind() {
        var chatClient = ChatClient.create(chatModel);
        String response = chatClient
                .prompt("remind me tomorrow 8 am to take my medicine")
                .tools(new CustomTools())
                .call()
                .content();
        System.out.println(response);
    }

    /**
     * 创建{@link ToolCallback}的实现,为大模型提供获取当前时间的tool
     * <p>
     * 实际上,每个通过注解提供给大模型的工具都会被包装成ToolCallback的实现
     * <p>
     * 使用ToolCallbacks.from(new DateTimeTools())方法, 可以将指定的类中对应方法都转化为ToolCallback[]
     * 使用到的方法: {@link CustomTools#getCurrentDateTime()}
     */
    @Test
    void setToolCallbackChatOptions() {
        ToolCallback[] dateTimeTools = ToolCallbacks.from(new CustomTools());
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(dateTimeTools)
                .build();
        Prompt prompt = new Prompt("What day is tomorrow?", chatOptions);
        chatModel.call(prompt);
    }

}
