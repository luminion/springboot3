package com.example.springboot21ai.config;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 多模态模型配置-阿里云百炼
 * 除了系统根据配置文件配置的ChatModel，还可以自定义多个不同多模态模型的ChatModel
 * <a href="https://bailian.console.aliyun.com/tab=model#/model-market">模型列表</a>
 *
 * @author bootystar
 */
@Configuration(proxyBeanMethods = false)
public class ChatModelConfig4Ali {


    /**
     * 通义千问-Max
     * 32K上下文
     * 适合复杂任务，能力最强
     * 
     * @param api api
     * @return {@link OpenAiChatModel }
     */
    @Bean
    public OpenAiChatModel qwen_max(@Qualifier("aliApi") OpenAiApi api) {
        // 该模型仅支持流式输出
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("qwen-max")
                        .build()
                )
                .build();
    }
    
    /**
     * 通义千问-Plus
     * 128K上下文
     * 效果、速度、成本均衡
     * 
     * @param api api
     * @return {@link OpenAiChatModel }
     */
    @Bean
    public OpenAiChatModel qwen_plus(@Qualifier("aliApi") OpenAiApi api) {
        // 该模型仅支持流式输出
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("qwen-plus")
                        .build()
                )
                .build();
    }

    /**
     * 通义千问-Turbo
     * 1M上下文
     * 适合简单任务，速度快、成本极低
     * 
     * @param api api
     * @return {@link OpenAiChatModel }
     */
    @Bean
    public OpenAiChatModel qwen_turbo(@Qualifier("aliApi") OpenAiApi api) {
        // 该模型仅支持流式输出
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("qwen-turbo")
                        .build()
                )
                .build();
    }

    /**
     * 通义千问-Long
     * 适合大规模文本分析，效果与速度均衡、成本较低
     *
     * @param api api
     * @return {@link OpenAiChatModel }
     */
    @Bean
    public OpenAiChatModel qwen_long(@Qualifier("aliApi") OpenAiApi api) {
        // 该模型仅支持流式输出
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("qwen-long")
                        .build()
                )
                .build();
    }
    



    /**
     * 通义千问-Omni-Turbo
     * 全模态
     * 仅支持以流式输出
     * 32K上下文
     * 通义千问全新多模态理解生成大模型，支持文本, 图像，语音，视频输入理解和混合输入理解，具备文本和语音同时流式生成能力
     * 输入的图片、音频、视频文件支持 Base64 编码与公网 URL 进行传入
     *
     * @param api {@link ApiConfig#aliApi()}
     * @return {@link OpenAiChatModel }
     */
    @Bean
    public OpenAiChatModel qwen_omni_turbo(@Qualifier("aliApi") OpenAiApi api) {
        // 该模型仅支持流式输出
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("qwen-omni-turbo")
                        .build()
                )
                .build();
    }


    /**
     * multimodal-embedding-v1通义实验室基于预训练多模态大模型构建的多模态向量模型
     * 免费
     * 该模型根据用户的输入生成高维连续向量，这些输入可以是文本、图片或视频
     *
     * @return {@link OpenAiChatModel }
     */
    @Bean
    public OpenAiChatModel multimodal_embedding_v1(@Qualifier("aliApi") OpenAiApi api) {
        // 该模型仅支持流式输出
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("multimodal-embedding-v1")
                        .build()
                )
                .build();
    }


}
