package com.example.ai.t10tool;

import com.example.ai.config.ChatClientConfig;
import com.example.ai.tool.WeatherService;
import com.example.ai.tool.WeatherTools;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.model.chat.client.autoconfigure.ChatClientBuilderConfigurer;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ChatModel chatModel;
    @Autowired
    private ChatClient chatClient;

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
     * <p>
     * 注意:
     * 此处有一个大坑
     * 只有使用官方自动配置的ChatClient.Builder()建造的ChatClient才能使用容器中的工具, 否则需要手动添加工具到ChatClient中
     * <p>
     * 如果引入了多个模型的依赖, 则需要手动指定具体的ChatModel来创建这个Builder的bean, 
     * 参考:{@link ChatClientConfig#chatClientBuilder(ChatClientBuilderConfigurer, DeepSeekChatModel, ObjectProvider, ObjectProvider)} 
     *
     * @see WeatherTools 配置函数Bean
     * @see ChatClientConfig 配置ChatClient
     */
    @Test
    void callWithSpringBean() {
        var content = chatClient
                .prompt("What's the weather like in chengdu?")
                .toolNames(WeatherTools.CURRENT_WEATHER_TOOL) // 指定工具名称
                .call()
                .content();
        System.out.println(content);
    }


}
