package com.example.ai.config;

import com.example.ai.constant.AiConst;
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
@Configuration(proxyBeanMethods = false)
public class ChatModelConfig4SiliconFlow {
    
    /**
     * DeepSeek-R1-0528-Qwen3-8B
     * 免费
     * 128K上下文
     * 是通过从 DeepSeek-R1-0528 模型蒸馏思维链到 Qwen3 8B Base 获得的模型
     *
     * @param api api
     * @return {@link OpenAiChatModel }
     */
    @Bean(AiConst.SILICONFLOW_DEEPSEEK_R1_0528_QWEN3_8B)
    public OpenAiChatModel deepseek_r1_0528_qwen3_8b(@Qualifier(AiConst.SILICONFLOW_API) OpenAiApi api) {
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(AiConst.SILICONFLOW_DEEPSEEK_R1_0528_QWEN3_8B)
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
    @Bean(AiConst.SILICONFLOW_QWEN3_8B)
    public OpenAiChatModel qwen3_8b(@Qualifier(AiConst.SILICONFLOW_API) OpenAiApi api) {
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(AiConst.SILICONFLOW_QWEN3_8B)
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
    @Bean(AiConst.SILICONFLOW_THUDM_GLM_41V_9B_THINKING)
    public OpenAiChatModel glm_41v_9b_thinking(@Qualifier(AiConst.SILICONFLOW_API) OpenAiApi api) {
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(AiConst.SILICONFLOW_THUDM_GLM_41V_9B_THINKING)
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
    @Bean(AiConst.SILICONFLOW_KWAI_KOLORS_KOLORS)
    public OpenAiChatModel kolors(@Qualifier(AiConst.SILICONFLOW_API) OpenAiApi api) {
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("Kwai-Kolors/Kolors")
                        .model(AiConst.SILICONFLOW_KWAI_KOLORS_KOLORS)
                        .build()
                )
                .build();
    }


}
