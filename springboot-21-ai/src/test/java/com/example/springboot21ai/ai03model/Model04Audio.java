package com.example.springboot21ai.ai03model;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.*;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.ai.openai.metadata.audio.OpenAiAudioSpeechResponseMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

/**
 * 音频生成模型
 * @author bootystar
 */
@SpringBootTest
public class Model04Audio {

    Resource audioResource = new ClassPathResource("/audio/joke-eng.wav");
    @Autowired
    private OpenAiAudioTranscriptionModel openAiTranscriptionModel;
    @Autowired
    private OpenAiAudioSpeechModel openAiAudioSpeechModel;

    /**
     * 音频识别
     */
    @Test
    void audioTranscription(){
        OpenAiAudioApi.TranscriptResponseFormat responseFormat = OpenAiAudioApi.TranscriptResponseFormat.VTT;
        OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                .language("en")
                .prompt("Ask not this, but ask that")
                .temperature(0f)
                .responseFormat(responseFormat)
                .build();
        AudioTranscriptionPrompt transcriptionRequest = 
                new AudioTranscriptionPrompt(audioResource, transcriptionOptions);
        AudioTranscriptionResponse response = openAiTranscriptionModel.call(transcriptionRequest);
        System.out.println(response.getResult().getOutput());
    }
    
    
    /**
     * 文字转语音
     */
    @Test
    @SneakyThrows
    void audioSpeech(){
        OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
                .model("tts-1")
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .speed(1.0f)
                .build();
        SpeechPrompt speechPrompt = new SpeechPrompt("Hello, this is a text-to-speech example.", speechOptions);
        SpeechResponse response = openAiAudioSpeechModel.call(speechPrompt);
        // Accessing metadata (rate limit info)
        OpenAiAudioSpeechResponseMetadata metadata = response.getMetadata();
        // audio byte
        byte[] responseAsBytes = response.getResult().getOutput();
        
        // 保存文件
        String fileName = UUID.randomUUID() + ".wav";
        var file = new File("./src/main/resources/audio/" + fileName);
        var fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(responseAsBytes);
        fileOutputStream.close();
        System.out.println(file.getAbsolutePath());
    }


    /**
     * 语音 API 支持使用分块传输编码进行实时音频流传输。
     * 这意味着音频可以在完整文件生成并可供访问之前播放
     */
    @Test
    @SneakyThrows
    void audioSpeechStream() {
        OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
                .speed(1.0f)
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .model(OpenAiAudioApi.TtsModel.TTS_1.value)
                .build();

        SpeechPrompt speechPrompt = new SpeechPrompt("Today is a wonderful day to build something people love!", speechOptions);

        Flux<SpeechResponse> responseStream = openAiAudioSpeechModel.stream(speechPrompt);
    }
}
