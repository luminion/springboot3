package com.example.ai.t03model;

import com.example.ai.constant.AiConst;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * 多模态模型
 * 多模态是指模型同时理解和处理来自各种来源的信息的能力，包括文本、图像、音频和其他数据格式
 * <p>
 * OpenAi视觉模型：
 * gpt-4
 * gpt-4o
 * gpt-4o-mini
 * <a href="https://platform.openai.com/docs/guides/vision">官网视觉模型</a>
 * <p>
 * OpenAi音频模型：
 * gpt-4o-audio-preview
 * <a href="https://platform.openai.com/docs/guides/audio">官网音频模型</a>
 *
 * @author bootystar
 */
@SpringBootTest
public class Model01Multimodal {
    
    @Autowired
    @Qualifier(AiConst.SILICONFLOW_THUDM_GLM_41V_9B_THINKING)
    private ChatModel glm4ChatModel;
    @Autowired
    @Qualifier(AiConst.ALI_QWEN_OMNI_TURBO)
    private ChatModel qwenOmniTurboChatModel;
    @Autowired
    @Qualifier(AiConst.OPENAI_GPT_4O_AUDIO_PREVIEW)
    private ChatModel gpt4oAudioPreviewChatModel;

    private final Resource imageResource = new ClassPathResource("/img/multimodal.test.png");
    private final Resource audioResource = new ClassPathResource("/audio/joke-eng.wav");


    /**
     * 使用chatModel调用图片识别
     */
    @Test
    void visionExplainByChatModel() {
        /*
        media 字段当前仅适用于用户输入消息（例如 UserMessage）。
        它对系统消息没有意义。
        包括 LLM 响应的 AssistantMessage 仅提供文本内容。
        要生成非文本媒体输出，您应该使用专用的单模态模型之一
         */
        var userMessage = UserMessage.builder()
                .text("Explain what do you see in this picture with chinese?")
                .media(new Media(MimeTypeUtils.IMAGE_PNG, imageResource))
                .build();
        var response = glm4ChatModel
                .call(new Prompt(userMessage));
        System.out.println(response.getResult().getOutput().getText());
    }

    /**
     * 图像识别
     */
    @Test
    void visionExplain() {
        var response = ChatClient.create(glm4ChatModel).prompt()
                .user(u -> u.text("Explain what do you see on this picture?")
                        .media(MimeTypeUtils.IMAGE_PNG, imageResource))
                .call()
                .content();
        System.out.println(response);
    }

    /**
     * 多张图像解释
     */
    @Test
    @SneakyThrows
    void multiVisionExplain() {
        // 使用网络资源图片
        var uri = URI.create("https://docs.spring.io/spring-ai/reference/_images/multimodal.test.png");
        var media = new Media(MimeTypeUtils.IMAGE_PNG, uri);
        // 使用本地资源图片
        var media1 = new Media(MimeTypeUtils.IMAGE_PNG, imageResource);
        var response = ChatClient.create(qwenOmniTurboChatModel).prompt()
                .user(u -> u.text("Explain what do you see on this picture? found difference between two images")
                        .media(media, media1))
                .stream()
                .content()
                .collectList()
                .block();
        System.out.println(String.join(" ", response));
    }

    /**
     * 识别音频
     */
    @Test
    void audioExplain() {
        /*
        音频内容：
        Why did the server love the Spring Framework?
        Because whenever it got into a dependency, Spring was always there to inject some relief!
         */
        
        var userMessage = UserMessage.builder()
                .text("What is this recording about? answer with chinese?")
                .media(new Media(MimeTypeUtils.parseMimeType("audio/wav"), audioResource))
                .build();
        var chatOptions = OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_4_O_AUDIO_PREVIEW)// 若当前使用的模型不支持音频，则需要切换模型
                .build();
        var prompt = Prompt.builder()
                .messages(userMessage)
//                .chatOptions(chatOptions)// 若当前使用的模型不支持音频，则需要配置该项
                .build();
        var response = gpt4oAudioPreviewChatModel
                .call(prompt);
        System.out.println(response.getResult().getOutput().getText());
    }

    /**
     * 生成音频文件
     */
    @Test
    @SneakyThrows
    void outputAudio() {
        var userMessage = UserMessage.builder()
                .text("Tell me joke about Java，answer with chinese")
                .build();
        var chatOptions = OpenAiChatOptions.builder()
//                .model(OpenAiApi.ChatModel.GPT_4_O_AUDIO_PREVIEW) // 若当前使用的模型不支持音频，则需要切换模型
                .outputModalities(List.of("text", "audio"))
                .outputAudio(new OpenAiApi.ChatCompletionRequest.AudioParameters(
                        OpenAiApi.ChatCompletionRequest.AudioParameters.Voice.ALLOY,
                        OpenAiApi.ChatCompletionRequest.AudioParameters.AudioResponseFormat.WAV))
                .build();
        var prompt = Prompt.builder()
                .messages(userMessage)
                .chatOptions(chatOptions)
                .build();
        ChatResponse response = gpt4oAudioPreviewChatModel
                .call(prompt) // 注意：音频参数不能使用stream调用
                ;
        // 输出文件内容
        String text = response.getResult().getOutput().getText(); // audio transcript
        // 音频字节流
        byte[] waveAudio = response.getResult().getOutput().getMedia().get(0).getDataAsByteArray(); // audio data
        // 保存文件
        String fileName = UUID.randomUUID() + ".wav";
        var file = new File("./src/main/resources/audio/" + fileName);
        var fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(waveAudio);
        fileOutputStream.close();

        System.out.println(text);
        System.out.println(file.getAbsolutePath());
    }

}
