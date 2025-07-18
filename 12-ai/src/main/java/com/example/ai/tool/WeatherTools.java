package com.example.ai.tool;

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
public class WeatherTools {
    public static final String CURRENT_WEATHER_TOOL = "currentWeather";

    @Bean(CURRENT_WEATHER_TOOL)
    @Description("Get the weather in location")
    Function<WeatherService.WeatherRequest, WeatherService.WeatherResponse> currentWeather() {
        return new WeatherService();
    }

}
