package com.example.ai.t02prompt;

import com.example.ai.constant.AiConst;
import com.example.ai.entity.ActorsFilms;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;

/**
 * 结构化输出
 * <a href="https://docs.spring.io/spring-ai/reference/api/structured-output-converter.html">Spring AI 结构化输出文档</a>
 * <p>
 * StructuredOutputConverter 接口允许您获取结构化输出，例如将输出映射到基于文本的 AI 模型输出中的 Java 类或值数组
 * 它结合了 Spring Converter<String、T> 接口和 FormatProvider 接口
 * FormatProvider 为 AI 模型提供特定的格式设置准则，使其能够生成文本输出，这些输出可以使用 Converter 转换为指定的目标类型T
 * <p>
 * spring提供了多个内置的 StructuredOutputConverter 实现，用于将 AI 模型的输出转换为 Java 对象
 * <p>
 * BeanOutputConverter<T>
 * 配置了指定的 Java 类（例如 Bean）或 ParameterizedTypeReference，此转换器采用 FormatProvider 实现，
 * 该实现指示 AI 模型生成符合 DRAFT_2020_12 的 JSON 响应，该响应从指定的 Java 类派生的 JSON 架构 。
 * 随后，它利用 ObjectMapper 将 JSON 输出反序列化为目标类的 Java 对象实例
 * 支持通过 @JsonPropertyOrder 注解在生成的 JSON 模式中进行自定义属性排序。此注释允许您指定属性在架构中出现的确切顺序，而不管它们在类或记录中的声明顺序如何
 * <p>
 * MapOutputConverter - 上级抽象AbstractMessageOutputConverter
 * 使用 FormatProvider 实现扩展其功能，该实现指导 AI 模型生成符合 RFC8259 的 JSON 响应。
 * 此外，它还包含一个转换器实现，该实现利用提供的 MessageConverter 将 JSON 有效负载转换为 java.util.Map<String， Object> 实例
 * <p>
 * ListOutputConverter - 上级抽象AbstractConversionServiceOutputConverter
 * 并包含为逗号分隔的列表输出量身定制的 FormatProvider 实现。转换器实现使用提供的 ConversionService 将模型文本输出转换为 java.util.List
 *
 * @author bootystar
 */
@SpringBootTest
public class Prompt02StructuredOutput {

    @Autowired
    private ChatModel chatModel;


    /**
     * 使用ChatClient将结果bean转化为bean
     */
    @Test
    void convert2BeanByChatClient() {
        ActorsFilms actorsFilms = ChatClient.create(chatModel).prompt()
                .user(u -> u.text("Generate the filmography of 5 movies for {actor}.")
                        .param("actor", "Tom Hanks"))
                .call()
                .entity(ActorsFilms.class);
        System.out.println(actorsFilms);
    }


    /**
     * 使用更底层的ChatModel将结果转化为bean
     */
    @Test
    void convertToBeanByChatModel() {
        BeanOutputConverter<ActorsFilms> beanOutputConverter = new BeanOutputConverter<>(ActorsFilms.class);
        String actor = "Tom Hanks";
        String template = """
                Generate the filmography of 5 movies for {actor}.
                {format}
                """;
        var prompt = PromptTemplate.builder()
                .template(template)
                .variables(Map.of("actor", actor, "format", beanOutputConverter.getFormat()))
                .build()
                .create();
        Generation generation = chatModel
                .call(prompt)
                .getResult();
        ActorsFilms actorsFilms = beanOutputConverter.convert(generation.getOutput().getText());
        System.out.println(actorsFilms);
    }


    /**
     * 使用ChatClient将结果bean转化为bean集合
     */
    @Test
    void convertToBeanListByChatClient() {
        List<ActorsFilms> actorsFilms = ChatClient.create(chatModel).prompt()
                .user("Generate the filmography of 5 movies for Tom Hanks and Bill Murray.")
                .call()
                .entity(new ParameterizedTypeReference<List<ActorsFilms>>() {
                });
        actorsFilms.forEach(System.out::println);
    }

    /**
     * 使用更底层的ChatModel将结果转化为bean集合
     */
    @Test
    void convertToBeanListByChatModel() {
        BeanOutputConverter<List<ActorsFilms>> outputConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<List<ActorsFilms>>() {
        });
        String template = """
                Generate the filmography of 5 movies for Tom Hanks and Bill Murray.
                {format}
                """;
        var prompt = PromptTemplate.builder()
                .template(template)
                .variables(Map.of("format", outputConverter.getFormat()))
                .build()
                .create();
        Generation generation = chatModel
                .call(prompt)
                .getResult();
        List<ActorsFilms> actorsFilms = outputConverter.convert(generation.getOutput().getText());
        actorsFilms.forEach(System.out::println);
    }


    /**
     * 使用ChatClient将结果bean转化为bean
     */
    @Test
    void convertToMapByChatClient() {
        Map<String, Object> result = ChatClient.create(chatModel).prompt()
                .user(u -> u.text("Provide me a List of {subject}")
                        .param("subject", "an array of numbers from 1 to 9 under they key name 'numbers'"))
                .call()
                .entity(new ParameterizedTypeReference<Map<String, Object>>() {
                });
        System.out.println(result);
    }


    /**
     * 使用更底层的ChatModel将结果转化为Map
     */
    @Test
    void convertToMapByChatModel() {
        MapOutputConverter mapOutputConverter = new MapOutputConverter();
        String template = """
                Provide me a List of {subject}
                {format}
                """;
        var prompt = PromptTemplate.builder()
                .template(template)
                .variables(Map.of("subject", "an array of numbers from 1 to 9 under they key name 'numbers'", "format", mapOutputConverter.getFormat()))
                .build()
                .create();
        Generation generation = chatModel
                .call(prompt)
                .getResult();
        Map<String, Object> result = mapOutputConverter.convert(generation.getOutput().getText());
        System.out.println(result);
    }


    /**
     * 让openAi 按照指定的json格式返回数据
     * OpenAI 提供自定义结构化输出 API，确保您的模型生成的响应严格符合您提供的 JSON 架构 。
     * 除了现有的 Spring AI 模型无关的结构化输出转换器之外，这些 API 还提供了增强的控制和精度
     */
    @Test
    void jsonParam() {
        String jsonSchema = """
                {
                    "type": "object",
                    "properties": {
                        "steps": {
                            "type": "array",
                            "items": {
                                "type": "object",
                                "properties": {
                                    "explanation": { "type": "string" },
                                    "output": { "type": "string" }
                                },
                                "required": ["explanation", "output"],
                                "additionalProperties": false
                            }
                        },
                        "final_answer": { "type": "string" }
                    },
                    "required": ["steps", "final_answer"],
                    "additionalProperties": false
                }
                """;

        Prompt prompt = new Prompt("how can I solve 8x + 7 = -23, response with chinese",
                OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.GPT_4_O_MINI)
                        .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
                        .build());

        ChatResponse response = chatModel.call(prompt);
        System.out.println(response.getResult().getOutput().getText());
    }


}
