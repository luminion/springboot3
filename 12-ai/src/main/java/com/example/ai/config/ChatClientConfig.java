package com.example.ai.config;

import com.example.ai.constant.AiConst;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.model.chat.client.autoconfigure.ChatClientAutoConfiguration;
import org.springframework.ai.model.chat.client.autoconfigure.ChatClientBuilderConfigurer;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ChatClient是对ChatModel的高级抽象，提供更高级的封装和更便捷的API
 * 系统默认没有配置ChatClient
 * 需要自己配置bean
 *
 * @author bootystar
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class ChatClientConfig {

    /**
     * 手动指定默认的ChatModel
     * 当引入单个AI模型依赖时, 系统会自动注入ChatModel
     * 但是当引入多个AI模型依赖时, 系统会注入多个不同类型的ChatModel, 
     * 此时需要手动指定默认的ChatModel
     *
     * @param chatModel 聊天模型
     * @return {@link ChatModel }
     */
    @Bean
    public ChatModel chatModel(@Qualifier(AiConst.ALI_QWEN_MAX) OpenAiChatModel chatModel) {
        return chatModel;
    }
    
    /**
     * 聊天客户端生成器
     * 当引入多个依赖时, Spring无法自动注入ChatClient.Builder
     * 此时, 需要手动创建指定具体的ChatModel, 并创建ChatClient.Builder, 以便使用Spring的自动配置
     * 若非通过此种途径添加的Builder, 将不具备Spring自动配置的功能, 例如自动添加函数Bean的ToolCall等
     * <p>
     *
     * @param chatClientBuilderConfigurer 聊天客户端生成器配置器
     * @param chatModel                   聊天模型
     * @param observationRegistry         观察登记处
     * @param observationConvention       观察公约
     * @return {@link ChatClient.Builder }
     * @see ChatClientAutoConfiguration#chatClientBuilder(ChatClientBuilderConfigurer, ChatModel, ObjectProvider, ObjectProvider)  Spring的自动配置方法
     */
    @Bean
    @SuppressWarnings("all")
    ChatClient.Builder chatClientBuilder(
            ChatClientBuilderConfigurer chatClientBuilderConfigurer, 
            ChatModel chatModel,
            ObjectProvider<ObservationRegistry> observationRegistry,
            ObjectProvider<ChatClientObservationConvention> observationConvention
    ) {
        ChatClient.Builder builder = ChatClient
                .builder(
                        chatModel,
                        observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP),
                        observationConvention.getIfUnique(() -> null
                        )
                );
        return chatClientBuilderConfigurer.configure(builder);
    }

    /**
     * 配置ChatClient
     * 在仅引入一种AI底层模型类型依赖时使用, 在仅引入一种AI底层模型类型依赖时spring会自动配置ChatClient.Builder
     * 若同时引入多个依赖，则不能使用该方法, 通过以下途径取代
     * 1.手动创建ChatClient, 参考:{@link ChatClientConfig#chatClient(DeepSeekChatModel)}
     * 2.新建并注入ChatClient.Builder, 参考:{@link ChatClientConfig#chatClientBuilder(ChatClientBuilderConfigurer, DeepSeekChatModel, ObjectProvider, ObjectProvider)}
     *
     * @param builder 建设者
     * @return {@link ChatClient }
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
//                .defaultSystem("""
//                         无要求的情况下, 请以中文为主进行回答
//                        """
//                ) // 默认的system提示词, 当未输入system时采用
                .build();
    }
    


}
