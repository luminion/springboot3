package com.example.springboot21ai.ai10toolcalling;

import com.example.springboot21ai.config.FunctionBeanConfig;
import com.example.springboot21ai.tool.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 将函数作为工具
 * <p>
 * Spring AI 提供了内置支持，用于从函数中指定工具
 * 1.通过FunctionToolCallback指定lambda表达式或方法引用
 * 2.传入某个函数方法的对应实例, 使用此项时可以结合@bean注入实例对象
 * <p>
 * 函数bean,返回值不能是以下类型
 * 基本类型
 * Optional
 * 集合类型（例如 List、Map、Array、Set）
 * 异步类型（例如 CompletableFuture、Future）
 * 响应式类型（例如 Flow、Mono、Flux）
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
     * 创建函数方法的ToolCallback实例
     * 使用函数实现类{@link WeatherService#apply(WeatherService.WeatherRequest)}
     */
    @Test
    void test() {
        ToolCallback toolCallback = FunctionToolCallback
                .builder("currentWeather", new WeatherService())
                .description("Get the weather in location")
                .inputType(WeatherService.WeatherRequest.class) // 输入参数类型， 必须指定
                .build();
        var content = ChatClient.create(chatModel)
                .prompt("what is the weather in beijing?")
                .toolCallbacks(toolCallback)
                .call()
                .content();
        System.out.println(content);
    }

    /**
     * 调用提前注入到容器中的函数
     * 需要在配置类中配置好函数bean{@link FunctionBeanConfig#currentWeather()}
     *
     * @deprecated todo 官方文档示例暂时跑不通, 后续处理
     * 提示
     * No @Tool annotated methods found in currentWeather.Did you mean to pass a ToolCallback or ToolCallbackProvider? 
     * If so, you have to use .toolCallbacks() instead of .tool()
     */
    @Test
    void callWithSpringBean() {
        var content = ChatClient.create(chatModel)
                .prompt("What's the weather like in Copenhagen?")
                .tools(FunctionBeanConfig.CURRENT_WEATHER_FUNCTION) // 直接传入bean名称即可
                .call()
                .content();
        System.out.println(content);

        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolNames(FunctionBeanConfig.CURRENT_WEATHER_FUNCTION)
                .build();
        Prompt prompt = new Prompt("What's the weather like in Copenhagen?", chatOptions);
        chatModel.call(prompt);
    }


}
