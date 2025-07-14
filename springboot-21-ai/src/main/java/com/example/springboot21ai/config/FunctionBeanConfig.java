package com.example.springboot21ai.config;

import com.example.springboot21ai.tool.WeatherService;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

/**
 * 函数bean配置
 * 可以将函数作为bean配置到容器中
 * 可以使用任何 Function、Supplier、Consumer、BiFunction 类型的bean作为工具
 * <p>
 * 这种工具规范方法的缺点是不能保证类型安全，因为工具解析是在运行时完成的。
 * 要缓解这种情况，您可以使用 @Bean 注释显式指定工具名称，并将值存储在常量中，以便您可以在聊天请求中使用它，而不是对工具名称进行硬编码
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
@Configuration(proxyBeanMethods = false)
public class FunctionBeanConfig {
    public static final String CURRENT_WEATHER_FUNCTION = "currentWeather";

    /**
     * 获取指定位置天气的函数
     * 注解{@link Description}用于提供工具的描述，模型使用它来了解何时以及如何调用该工具
     * 如果您未提供描述，则方法名称将用作工具描述。但是，强烈建议提供详细的描述，因为这对于模型了解工具的用途以及如何使用它至关重要
     * <p>
     * 注解{@link ToolParam} 提供有关输入参数的其他信息，例如描述或参数是必需的还是可选的。
     * 示例：{@link WeatherService.WeatherRequest}
     * 默认情况下，所有输入参数都被视为必需参数
     *
     * @return {@link Function }
     */
    @Bean(CURRENT_WEATHER_FUNCTION)
    @Description("Get the weather in location")
    public Function<WeatherService.WeatherRequest, WeatherService.WeatherResponse> currentWeather() {
        return new WeatherService();
    }

}
