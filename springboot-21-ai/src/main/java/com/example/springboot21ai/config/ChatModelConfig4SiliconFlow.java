package com.example.springboot21ai.config;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 多模态模型配置-硅基流动
 * 除了系统根据配置文件配置的ChatModel，还可以自定义多个不同多模态模型的ChatModel
 * <a href="https://cloud.siliconflow.cn/sft-j2or9rrdgo/models">模型列表</a>
 *
 * @author bootystar
 */
@Configuration
public class ChatModelConfig4SiliconFlow {

    /**
     * 默认模型
     * Qwen/Qwen3-8B
     * 免费
     * 128K上下文
     * Qwen3-8B 是通义千问系列的最新大语言模型
     *
     * @param api api
     * @return {@link OpenAiChatModel }
     */
    @Bean
    public OpenAiChatModel baseChatModel(@Qualifier("siliconFlowApi") OpenAiApi api) {
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("Qwen/Qwen3-8B")
                        .build()
                )
                .build();
    }

    /**
     * DeepSeek-R1-0528-Qwen3-8B
     * 免费
     * 128K上下文
     * 是通过从 DeepSeek-R1-0528 模型蒸馏思维链到 Qwen3 8B Base 获得的模型
     *
     * @param api api
     * @return {@link OpenAiChatModel }
     */
    @Bean
    public OpenAiChatModel deepseek_r1_0528_qwen3_8b(@Qualifier("siliconFlowApi") OpenAiApi api) {
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("deepseek-ai/DeepSeek-R1-0528-Qwen3-8B")
                        .build()
                )
                .build();
    }

    /**
     * Qwen/Qwen3-8B
     * 免费
     * 128K上下文
     * Qwen3-8B 是通义千问系列的最新大语言模型
     *
     * @param api api
     * @return {@link OpenAiChatModel }
     */
    @Bean
    public OpenAiChatModel qwen3_8b(@Qualifier("siliconFlowApi") OpenAiApi api) {
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("Qwen/Qwen3-8B")
                        .build()
                )
                .build();
    }


    /**
     * THUDM/GLM-4.1V-9B-Thinking
     * 免费
     * 图片识别
     * 64K上下文
     * 智谱 AI 和清华大学 KEG 实验室联合发布的一款开源视觉语言模型 支持视觉输入
     *
     * @param api api
     * @return {@link OpenAiChatModel }
     */
    @Bean
    public OpenAiChatModel glm_41v_9b_thinking(@Qualifier("siliconFlowApi") OpenAiApi api) {
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("THUDM/GLM-4.1V-9B-Thinking")
                        .build()
                )
                .build();
    }

    /**
     * Kwai-Kolors/Kolors
     * 免费
     * 图片生成
     * 快手 Kolors 团队开发的基于潜在扩散的大规模文本到图像生成模型
     *
     * @param api api
     * @return {@link OpenAiChatModel }
     */
    @Bean
    public OpenAiChatModel kolors(@Qualifier("siliconFlowApi") OpenAiApi api) {
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("Kwai-Kolors/Kolors")
                        .build()
                )
                .build();
    }


}
