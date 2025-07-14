package com.example.springboot21ai.ai10toolcalling;

import com.example.springboot21ai.entity.Customer;
import com.example.springboot21ai.tool.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 将方法作为工具
 * <p>
 * Spring AI 提供了两种方式来指定方法作为工具
 * <p>
 * 1.通过注解{@link Tool} 指定方法, 并结合{@link ToolParam}描述该方法的作用及参数
 * <p>
 * 2.创建{@link ToolCallback}的实现
 * <p>
 * 您可以为方法定义任意数量的参数（包括无参数），并且大多数类型都可以使用（原始类型、POJO、枚举、列表、数组、映射等）。
 * 类似地，方法可以返回大多数类型，包括 void。
 * 如果方法返回值，返回类型必须是一个可序列化类型，因为结果将被序列化并发送回模型
 * 以下类型目前不能作为工具方法使用的参数或返回类型
 * Optional
 * 异步类型( CompletableFuture 、 Future ),
 * 响应式类型（例如 Flow 、 Mono 、 Flux ）
 * 函数式类型（例如 Function 、 Supplier 、 Consumer ）
 *
 * @author bootystar
 * @see DateTimeTools 根据注解创建的工具
 */
@SpringBootTest
public class ToolCall02MethodCall {

    @Autowired
    @Qualifier("qwen_plus")
    private ChatModel chatModel;

    /**
     * 创建{@link ToolCallback}的实现,为大模型提供获取当前时间的tool
     * <p>
     * 实际上,每个通过注解提供给大模型的工具都会被包装成ToolCallback的实现
     * <p>
     * 使用ToolCallbacks.from(new DateTimeTools())方法, 可以将指定的类中对应方法都转化为ToolCallback[]
     * 使用到的方法: {@link DateTimeTools#getCurrentDateTime()}
     */
    @Test
    void setToolCallbackChatOptions() {
        ToolCallback[] dateTimeTools = ToolCallbacks.from(new DateTimeTools());
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(dateTimeTools)
                .build();
        Prompt prompt = new Prompt("What day is tomorrow?", chatOptions);
        chatModel.call(prompt);
    }
    
    
    /**
     * 调用参数类型为实体类的工具, 根据实体类中注解自动生成工具
     * <p>
     * 注意,参数需要加上注解说明
     * 方法:{@link CustomerTools#saveCustomerInfo(Customer)}
     */
    @Test
    void toolCallbackWithEntityParams() {
        var resp = ChatClient.create(chatModel)
                .prompt()
                .user("generate a customer info, and save it to database")
                .tools(new CustomerTools())
                .call()
                .content();
        System.out.println(resp);
    }


}
