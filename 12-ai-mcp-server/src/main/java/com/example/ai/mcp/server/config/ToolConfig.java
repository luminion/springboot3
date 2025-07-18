package com.example.ai.mcp.server.config;

import com.example.ai.mcp.server.tools.WeatherService;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 暴露给客户端的工具
 * 自动配置将自动检测并注册所有工具回调，来源包括：
 * 1.ToolCallback bean 
 * 2.ToolCallback的List bean 
 * 3.ToolCallbackProvider bean
 * 工具按名称去重，使用每个工具只保留第一个定义
 *
 * @author bootystar
 */
@Configuration(proxyBeanMethods = false)
public class ToolConfig {

    @Bean
    public ToolCallbackProvider weatherTools(WeatherService weatherService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(weatherService)
                .build();
    }

    public record TextInput(String input) { }

    @Bean
    public ToolCallback toUpperCase() {
        return FunctionToolCallback.builder("toUpperCase", (TextInput input) -> input.input().toUpperCase())
                .inputType(TextInput.class)
                .description("Put the text to upper case")
                .build();
    }
    
//    @Bean
    public ToolCallbackProvider myTools(WeatherService weatherService) {
        var toolCallbacks = ToolCallbacks.from(
                weatherService
        );
        return ToolCallbackProvider.from(toolCallbacks);
    }
    
}
