package com.example.ai.config;

import com.example.ai.constant.AiConst;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
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

    /**
     * BAAI/bge-large-zh-v1.5 是一个大型中文文本嵌入模型，是 BGE (BAAI General Embedding) 系列的一部分
     * 免费
     * 该模型在 C-MTEB 基准测试中表现出色，在 31 个数据集上的平均得分为 64.53，在检索、语义相似度、文本对分类等多个任务中都取得了优异成绩。
     * 它支持最大 512 个 token 的输入长度，适用于各种中文自然语言处理任务，如文本检索、语义相似度计算等
     * 0.5K上下文
     * 向量维度1024
     *
     * @return {@link OpenAiChatModel }
     */
//    @Bean(AiConst.SILICONFLOW_BAAI_BGE_LARGE_ZH_V1_5)
    public OpenAiEmbeddingModel baai_bge_large_zh_v1_5(@Qualifier(AiConst.SILICONFLOW_API) OpenAiApi api) {
        var embeddingOptions = OpenAiEmbeddingOptions.builder()
                .model(AiConst.SILICONFLOW_BAAI_BGE_LARGE_ZH_V1_5)
                .build();
        return new OpenAiEmbeddingModel(
                api,
                MetadataMode.ALL,
                embeddingOptions
        );
    }


    /**
     * BGE-M3 是一个多功能、多语言、多粒度的文本嵌入模型。
     * 它支持三种常见的检索功能：密集检索、多向量检索和稀疏检索。该模型可以处理超过100种语言，并且能够处理从短句到长达8192个词元的长文档等不同粒度的输入。
     * 免费
     * 8k上下文
     * 向量维度1024
     * 
     * @param api api
     * @return {@link OpenAiEmbeddingModel }
     */
//    @Bean(AiConst.SILICONFLOW_BAAI_BGE_M3)
    public OpenAiEmbeddingModel baai_bge_m3(@Qualifier(AiConst.SILICONFLOW_API) OpenAiApi api) {
        var embeddingOptions = OpenAiEmbeddingOptions.builder()
                .model(AiConst.SILICONFLOW_BAAI_BGE_M3)
                .build();
        return new OpenAiEmbeddingModel(
                api,
                MetadataMode.ALL,
                embeddingOptions
        );
    }


}
