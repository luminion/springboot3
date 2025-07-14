package com.example.springboot21ai.ai10toolcalling;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.resolution.DelegatingToolCallbackResolver;
import org.springframework.ai.tool.resolution.StaticToolCallbackResolver;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 将工具传递给模型的主要方法是在调用 ChatClient 或 ChatModel 时提供 ToolCallback，使用方法作为工具和函数作为工具中描述的策略之一
 * 但是，Spring AI 还支持在运行时使用 ToolCallbackResolver 接口动态解析工具
 * <p>
 * 在客户端，您将工具名称提供给 ChatClient 或 ChatModel，而不是 ToolCallback
 * 在服务器端，ToolCallbackResolver 实现负责将工具名称解析为相应的 ToolCallback 实例
 * <p>
 * 默认情况下，Spring AI 依赖于 DelegatingToolCallbackResolver, 并有以下2个ToolCallbackResolver
 * <p>
 * 1.SpringBeanToolCallbackResolver
 * Function、Supplier、Consumer 或 BiFunction 类型的 Spring bean 中的工具。也就是将函数方法注入到spring中使用的解析器
 * <p>
 * 2.StaticToolCallbackResolver
 * 从 ToolCallback 实例的静态列表中解析工具。当使用 Spring Boot 自动配置时，此解析器会自动配置在应用程序上下文中定义的所有类型为 ToolCallback 的 bean
 *
 * @author bootystar
 * @see org.springframework.ai.tool.resolution.ToolCallbackResolver
 */
@SpringBootTest
public class ToolCall09Resolution {
    @Autowired
    @Qualifier("qwen_plus")
    private ChatModel chatModel;

    /**
     * 手动配置解析器
     *
     * @param toolCallbacks 工具回调
     * @return {@link ToolCallbackResolver }
     */
//    @Bean
    ToolCallbackResolver toolCallbackResolver(List<ToolCallback> toolCallbacks) {
        StaticToolCallbackResolver staticToolCallbackResolver = new StaticToolCallbackResolver(toolCallbacks);
        return new DelegatingToolCallbackResolver(List.of(staticToolCallbackResolver));
    }
}
