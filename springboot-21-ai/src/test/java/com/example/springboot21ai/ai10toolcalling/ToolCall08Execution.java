package com.example.springboot21ai.ai10toolcalling;

import com.example.springboot21ai.tool.CustomerTools;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ToolCall的调用在Spring中默认自动化的
 * 实际每次调用工具后, 都会发送一次新的请求
 * 工具执行是使用提供的输入参数调用工具并返回结果的过程。工具执行由 ToolCallingManager 接口处理，该接口负责管理工具执行生命周期
 * <p>
 * 当使用默认行为时， Spring AI 将自动拦截来自模型的任何工具调用请求，调用工具并将结果返回给模型。
 * <p>
 * 所有这些都是由每个 ChatModel 实现使用 ToolCallingManager 透明地为您完成的, 过程如下
 * <p>
 * 1.我们想要为模型提供工具时，我们会将其定义包含在聊天请求 （Prompt） 中，并调用 ChatModel API，该 API 将请求发送到 AI 模型
 * <p>
 * 2.当模型决定调用工具时，它会发送一个响应 （ChatResponse），其中包含工具名称和根据定义的架构建模的输入参数
 * <p>
 * 3.ChatModel 将工具调用请求发送到 ToolCallingManager API
 * <p>
 * 4.ToolCallingManager 负责识别要调用的工具，并使用提供的输入参数执行它
 * <p>
 * 5.工具调用的结果将返回到 ToolCallingManager
 * <p>
 * 6.ToolCallingManager 将工具执行结果返回给 ChatModel
 * <p>
 * 7.ChatModel 将工具执行结果发送回 AI 模型 （ToolResponseMessage）
 * <p>
 * 8.AI 模型使用工具调用结果作为附加上下文生成最终响应，并通过 ChatClient 将其发送回调用方 （ChatResponse）
 *
 * @author bootystar
 */
@SpringBootTest
public class ToolCall08Execution {
    @Autowired
    @Qualifier("qwen_plus")
    private ChatModel chatModel;

    /**
     * User-Controlled Tool Execution用户控制ToolCall
     * <p>
     * 确定工具调用是否符合执行条件的逻辑由 ToolExecutionEligibilityPredicate 接口处理。
     * 默认情况下, 当满足以下条件时, SpringAi会自动调用ToolCall
     * <p>
     * 1.ToolCallingChatOptions 的 internalToolExecutionEnabled 属性是否设置为 true（默认值）
     * <p>
     * 2.ChatResponse.hasToolCalls()的长度不为0 (即大模型判断要调用的工具列表)
     * <p>
     * 以下为基本的手动控制调用示例
     */
    @Test
    void userControlledToolExecution() {
        ToolCallingManager toolCallingManager = ToolCallingManager.builder().build();
        var toolCallbacks = ToolCallbacks.from(new CustomerTools());
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(toolCallbacks)
                .internalToolExecutionEnabled(false) // 禁用工具自动调用
                .build();
        Prompt prompt = new Prompt("Tell me more about the customer with ID 42", chatOptions);

        ChatResponse chatResponse = chatModel.call(prompt);

        // 当需要调用ToolCall时, 判断是否允许调用
        while (chatResponse.hasToolCalls()) {
            // 手动调用ToolCall
            ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, chatResponse);
            // 将历史记录发送给模型
            prompt = new Prompt(toolExecutionResult.conversationHistory(), chatOptions);
            // 再次调用模型, 让模型输出最终结果, 或调用下一个工具
            chatResponse = chatModel.call(prompt);
        }
        System.out.println(chatResponse.getResult().getOutput().getText());
    }

    /**
     * 当工具调用失败时，异常将作为 ToolExecutionException 传播，可以捕获该异常以处理错误。
     * A ToolExecutionExceptionProcessor 可用于处理 ToolExecutionException，
     * 结果有两种：
     * 1.生成要发送回 AI 模型的错误消息，
     * 2.引发要由调用方处理的异常
     * DefaultToolExecutionExceptionProcessor 则是 ToolExecutionExceptionProcessor 接口的自动配置实现。
     * 默认情况下，错误消息将发送回模型。
     * 构造函数 DefaultToolExecutionExceptionProcessor 允许您将 alwaysThrow 属性设置为 true 或 false。
     * 如果为 true，则将引发异常，而不是将错误消息发送回模型
     * <p>
     * 注意:
     * 如果您定义了自己的 ToolCallback 实现，请确保在 call（） 方法中的工具执行逻辑中发生错误时抛出 ToolExecutionException
     *
     * @see org.springframework.ai.tool.execution.ToolExecutionException
     * @see org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor
     */
    @Test
    void toolExecutionExceptionProcessor() {
        // 将工具异常返回给模型的处理器, 默认为此bean
        var processor = new DefaultToolExecutionExceptionProcessor(false);
        // 将工具异常抛出的处理器, 可以注入此处理器为bean,来改变行为
        var processor1 = new DefaultToolExecutionExceptionProcessor(true);

    }


}
