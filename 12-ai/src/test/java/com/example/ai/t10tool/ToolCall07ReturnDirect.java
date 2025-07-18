package com.example.ai.t10tool;

import com.example.ai.tool.CustomerTools;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ReflectionUtils;

/**
 * 默认情况下，工具调用的结果将作为响应发送回模型。然后，模型可以使用结果继续对话
 * 在某些情况下，您宁愿将结果直接返回给调用方，而不是将其发送回模型。
 * 例如，如果您构建了一个依赖于 RAG 工具的代理，您可能希望将结果直接返回给调用方，而不是将其发送回模型进行不必要的后处理。
 * 或者，也许您有一些工具可以结束代理的推理循环
 * 每个 ToolCallback 实现都可以定义是应将工具调用的结果直接返回给调用方还是发送回模型。默认情况下，结果将发送回模型。但是，您可以按工具更改此行为
 * 负责管理工具执行生命周期的 ToolCallingManager 负责处理与工具关联的 returnDirect 属性。
 * 如果该属性设置为 true，则工具调用的结果将直接返回给调用方。否则，结果将发送回模型
 * @author bootystar
 */
@SpringBootTest
public class ToolCall07ReturnDirect {
    @Autowired
    private ChatModel chatModel;


    /**
     * 测试一下
     * {@link CustomerTools#refreshSysCustomerInfo()}
     */
    @Test
    void test(){
        ToolMetadata toolMetadata = ToolMetadata.builder()
                .returnDirect(true)
                .build();
        
        ToolCallback toolCallback = MethodToolCallback.builder()
                .toolDefinition(ToolDefinition.builder()
                        .name("refreshSysCustomerInfo")
                        .description("Refresh System Default CustomerInfos")
                        .inputSchema("null")
                        .build())
                .toolMetadata(toolMetadata)
                .toolMethod(ReflectionUtils.findMethod(CustomerTools.class, "refreshSysCustomerInfo"))
                .toolObject(new CustomerTools())
                .build();
        var content = ChatClient.create(chatModel)
                .prompt("refresh the customer information for customer , can you get info from tool, if you can , tell me the info")
                .toolCallbacks(toolCallback)
                .call()
                .content();
        System.out.println(content);
    }
}
