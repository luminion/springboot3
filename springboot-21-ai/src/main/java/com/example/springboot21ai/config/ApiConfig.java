package com.example.springboot21ai.config;

import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 大模型平台配置
 * 当需要使用多个平台不同模型时可以配置
 * 也可以跳过该配置， 直接使用不同平台的ChatModel
 *
 * @author bootystar
 */
@Configuration(proxyBeanMethods = false)
public class ApiConfig {

    /**
     * openAI api
     *
     * @return {@link OpenAiApi }
     */
    @Bean
    public OpenAiApi openAiApi() {
        return OpenAiApi.builder()
                .baseUrl("https://api.chatanywhere.tech")
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();
    }

    /**
     * 阿里百炼 api
     *
     * @return {@link OpenAiApi }
     */
    @Bean
    public OpenAiApi aliApi() {
        return OpenAiApi.builder()
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode")
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .build();
    }

    /**
     * 硅基流动api
     *
     * @return {@link OpenAiApi }
     */
    @Bean
    public OpenAiApi siliconFlowApi() {
        return OpenAiApi.builder()
                .baseUrl("https://api.siliconflow.cn")
                .apiKey(System.getenv("SILICONFLOW_API_KEY"))
                .build();
    }

    /**
     * deepseek api
     * 暂时使用硅基流动的
     *
     * @return {@link DeepSeekApi }
     */
    @Bean
    public DeepSeekApi deepSeekApi() {
        return DeepSeekApi.builder()
                // 使用openai时, base-url配置时，要把/v1去掉，其他类型的，要加上V1
                .baseUrl("https://api.siliconflow.cn/v1")  
                .apiKey(System.getenv("SILICONFLOW_API_KEY"))
                .build();
    }


}
