package com.example.springboot21ai.ai10toolcalling;

import com.example.springboot21ai.tool.DateTimeTools;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

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
 *
 * @author bootystar
 * @see DateTimeTools 根据注解创建的工具
 */
@SpringBootTest
public class ToolCall01 {

    @Autowired
    @Qualifier("qwen_plus")
    private ChatModel chatModel;

    /**
     * 通过注解{@link Tool} 指定方法, 为大模型提供获取当前时间的tool
     * <p>
     * 通过注解指定方法:{@link DateTimeTools#getCurrentDateTime()}
     */
    @Test
    void setTomorrowRemind() {
        var chatClient = ChatClient.create(chatModel);
        String response = chatClient
                .prompt("remind me tomorrow 8 am to take my medicine")
                .tools(new DateTimeTools()) // 使用工具
                .call()
                .content();
        System.out.println(response);
    }


    /**
     * 为ChatClient 设置默认的工具
     */
    @Test
    void setDefaultTool(){
        var chatClient = ChatClient.builder(chatModel)
                .defaultTools(new DateTimeTools()) // 设置默认工具
                .build();
        var content = chatClient
                .prompt("remind me tomorrow 8 am to take my medicine")
                .call()
                .content();
        System.out.println(content);
    }

}
