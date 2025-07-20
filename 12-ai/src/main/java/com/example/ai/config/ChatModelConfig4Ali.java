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
    @Bean(AiConst.ALI_QWEN_MAX)
    public OpenAiChatModel qwen_max(@Qualifier(AiConst.ALI_API) OpenAiApi api) {
        // 该模型仅支持流式输出
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(AiConst.ALI_QWEN_MAX)
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
    @Bean(AiConst.ALI_QWEN_PLUS)
    public OpenAiChatModel qwen_plus(@Qualifier(AiConst.ALI_API) OpenAiApi api) {
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(AiConst.ALI_QWEN_PLUS)
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
    @Bean(AiConst.ALI_QWEN_TURBO)
    public OpenAiChatModel qwen_turbo(@Qualifier(AiConst.ALI_API) OpenAiApi api) {
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(AiConst.ALI_QWEN_TURBO)
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
    @Bean(AiConst.ALI_QWEN_LONG)
    public OpenAiChatModel qwen_long(@Qualifier(AiConst.ALI_API) OpenAiApi api) {
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(AiConst.ALI_QWEN_LONG)
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
    @Bean(AiConst.ALI_QWEN_OMNI_TURBO)
    public OpenAiChatModel qwen_omni_turbo(@Qualifier(AiConst.ALI_API) OpenAiApi api) {
        // 该模型仅支持流式输出
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(AiConst.ALI_QWEN_OMNI_TURBO)
                        .build()
                )
                .build();
    }


    /**
     * multimodal-embedding-v1通义实验室基于预训练多模态大模型构建的多模态向量模型
     * 免费
     * 该模型根据用户的输入生成高维连续向量，这些输入可以是文本、图片或视频
     * 向量维度1024
     * @deprecated 特殊模型, 不兼容
     *
     * @return {@link OpenAiChatModel }
     */
//    @Bean(AiConst.ALI_MULTIMODAL_EMBEDDING_V1)
    public OpenAiEmbeddingModel multimodal_embedding_v1(@Qualifier(AiConst.ALI_API) OpenAiApi api) {
        var embeddingOptions = OpenAiEmbeddingOptions.builder()
                .model(AiConst.ALI_MULTIMODAL_EMBEDDING_V1)
                .build();
        return new OpenAiEmbeddingModel(
                api,
                MetadataMode.ALL,
                embeddingOptions
        );
    }

    /**
     * text_embedding_v4
     * 是通义实验室基于Qwen3训练的多语言文本统一向量模型，相较V3版本在文本检索、聚类、分类性能大幅提升；在MTEB多语言、中英、Code检索等评测任务上效果提升15%~40%；
     * 支持64~2048维用户自定义向量维度
     * 向量维度:
     * 2,048、1,536、1,024（默认）、768、512、256、128、64
     * 最大行数10
     *
     * @param api api
     * @return {@link OpenAiEmbeddingModel }
     * @deprecated 不兼容openAI
     */
//    @Bean(AiConst.ALI_TEXT_EMBEDDING_V4)
    public OpenAiEmbeddingModel text_embedding_v4(@Qualifier(AiConst.ALI_API) OpenAiApi api) {
        var embeddingOptions = OpenAiEmbeddingOptions.builder()
                .model(AiConst.ALI_TEXT_EMBEDDING_V4)
                .dimensions(1536) // 此处需要和向量数据库的向量维度一致
                .build();
        return new OpenAiEmbeddingModel(
                api,
                MetadataMode.ALL,
                embeddingOptions
        );
    }


    /**
     * text_embedding_sync_v2
     * 通用文本向量的批处理接口，通过这个接口客户可以以文本方式一次性的提交大批量的向量计算请求，在系统完成所有的计算之后，大模型服务平台会将结果信息存储在结果文件中供客户下载解析。
     * 向量维度: 1536
     * 最大行数:100000
     *
     * @param api api
     * @return {@link OpenAiEmbeddingModel }
     * @deprecated 不兼容openAI
     */
//    @Bean(AiConst.ALI_TEXT_EMBEDDING_ASYNC_V2)
    public OpenAiEmbeddingModel text_embedding_sync_v2(@Qualifier(AiConst.ALI_API) OpenAiApi api) {
        var embeddingOptions = OpenAiEmbeddingOptions.builder()
                .model(AiConst.ALI_TEXT_EMBEDDING_ASYNC_V2)
                .build();
        return new OpenAiEmbeddingModel(
                api,
                MetadataMode.ALL,
                embeddingOptions
        );
    }

    /**
     * text_embedding_v2
     * 通用文本向量，是通义实验室基于LLM底座的多语言文本统一向量模型，面向全球多个主流语种，提供高水准的向量服务，帮助开发者将文本数据快速转换为高质量的向量数据
     * 向量维度: 1536
     * 最大行数: 25
     *
     * @param api api
     * @return {@link OpenAiEmbeddingModel }
     */
    @Bean(AiConst.ALI_TEXT_EMBEDDING_V2)
    public OpenAiEmbeddingModel text_embedding_v2(@Qualifier(AiConst.ALI_API) OpenAiApi api) {
        var embeddingOptions = OpenAiEmbeddingOptions.builder()
                .model(AiConst.ALI_TEXT_EMBEDDING_V2)
                .build();
        return new OpenAiEmbeddingModel(
                api,
                MetadataMode.ALL,
                embeddingOptions
        );
    }


}
