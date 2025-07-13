package com.example.springboot21ai.ai08toolcalling;

import com.example.springboot21ai.tool.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 将函数作为工具
 * <p>
 * Spring AI 提供了内置支持，用于从函数中指定工具，
 * 1.可以通过低级 FunctionToolCallback 实现以编程方式指定
 * 2.作为在运行时解析的 @Bean动态指定
 *
 * @author bootystar
 */
@SpringBootTest
public class ToolCall03FunctionCall {

    @Autowired
    @Qualifier("qwen_plus")
    private ChatModel chatModel;

    /**
     * 函数式方法的ToolCallback
     * 使用lambda表达式
     */
    @Test
    void functionToolCallback() {
        // 创建函数式工具
        var functionToolCallback = FunctionToolCallback
                .builder("getCurrentTimeMillis", System::currentTimeMillis)
                .description("Get the current Timestamp in the user's timezone")
                .build();

        var content = ChatClient.create(chatModel)
                .prompt("when is it now?")
                .toolCallbacks(functionToolCallback)
                .call()
                .content();
        System.out.println(content);
    }

    /**
     * 函数式方法的ToolCallback
     * 使用函数实现类{@link WeatherService#apply(WeatherService.WeatherRequest)}
     */
    @Test
    void test() {
        ToolCallback toolCallback = FunctionToolCallback
                .builder("currentWeather", new WeatherService())
                .description("Get the weather in location")
                .inputType(WeatherService.WeatherRequest.class) // 指定输入参数类型
                .build();
        var content = ChatClient.create(chatModel)
                .prompt("what is the weather in beijing?")
                .toolCallbacks(toolCallback)
                .call()
                .content();
        System.out.println(content);
    }

}
