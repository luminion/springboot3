package com.example.ai.config;

import com.example.ai.constant.AiConst;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 多模态模型配置-openAi
 * 除了系统根据配置文件配置的ChatModel，还可以自定义多个不同多模态模型的ChatModel
 * <a href="https://platform.openai.com/docs/models">模型列表</a>
 *
 * @author bootystar
 */
@Configuration(proxyBeanMethods = false)
public class ChatModelConfig4OpenAi {

    /**
     * gpt-4o-mini
     * 文本/图像输入
     * 128K上下文
     * GPT-4o迷你是一个快速，负担得起的小模型为重点任务。它接受文本和图像输入并产生文本输出 (包括结构化输出)。
     * 它是微调的理想选择，像GPT-4o这样的大型模型的模型输出可以被提取到GPT-4o-mini，以更低的成本和延迟产生类似的结果
     *
     * @param api {@link ApiConfig#openAiApi()}
     * @return {@link ChatModel }
     */
    @Bean(AiConst.OPENAI_GPT_4O_MINI)
    public OpenAiChatModel gpt_4o_mini(@Qualifier(AiConst.OPENAI_API) OpenAiApi api) {
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.GPT_4_O_MINI)
                        .build()
                )
                .build();
    }

    /**
     * gpt-4o-audio-preview
     * 支持音频处理的模型
     * 128K上下文
     *
     * @param api {@link ApiConfig#openAiApi()}
     * @return {@link ChatModel }
     */
    @Bean(AiConst.OPENAI_GPT_4O_AUDIO_PREVIEW)
    public OpenAiChatModel gpt_4o_audio_preview(@Qualifier(AiConst.OPENAI_API) OpenAiApi api) {
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.GPT_4_O_AUDIO_PREVIEW) // 支持音频处理的模型
                        .build()
                )
                .build();
    }


}
