package com.example.springboot21ai.ai10toolcalling;

import com.example.springboot21ai.entity.ActorsFilms;
import com.example.springboot21ai.tool.DateTimeTools;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.util.json.schema.JsonSchemaGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 向 AI 模型提供工具时，模型需要知道用于调用该工具的输入类型的架构。架构用于了解如何调用工具和准备工具请求。
 * <p>
 * Spring AI 提供了内置支持，用于通过 {@link JsonSchemaGenerator} 类为工具生成 input 类型的 JSON Schema。该架构作为 ToolDefinition 的一部分提供
 * JsonSchemaGenerator 类在后台用于为方法或函数的输入参数生成 JSON 架构，使用方法作为工具和函数作为工具中描述的任何策略。
 * JSON 架构生成逻辑支持一系列注释，您可以在方法和函数的输入参数上使用这些注释来自定义生成的架构
 * <p>
 * 除了提供工具本身的描述外，您还可以提供工具输入参数的描述。
 * 描述可用于提供有关输入参数的关键信息，例如参数应采用的格式、允许的值等。
 * 这有助于模型了解输入架构以及如何使用它。
 * Spring AI 提供了内置支持，以使用以下注释之一为 Importing 参数生成描述
 * 1.@ToolParam(description = "…") 来自Spring AI
 * 2.@JsonClassDescription(description = "…") 来自Jackson
 * 3.@JsonPropertyDescription(description = "…") 来自Jackson
 * 3.@Schema(description = "…") 来自swagger
 * <p>
 * 默认情况下，每个输入参数都被视为必需参数，这会强制 AI 模型在调用工具时为其提供值。但是，您可以使用以下注释之一将 input 参数设置为可选，按以下优先顺序：
 * 1.@ToolParam(required = false) 来自Spring AI
 * 2.@JsonProperty(required = false) 来自Jackson
 * 3.@Schema(required = false) 来自swagger
 * 4.@Nullable 来自Spring Framework
 *
 * @author bootystar
 */
@SpringBootTest
public class ToolCall05JsonSchema {
    
    @Autowired
    @Qualifier("qwen_plus")
    private ChatModel chatModel;

    /**
     * 生成json参数说明
     */
    @Test
    void generateJsonSchema() {
        Method method = ReflectionUtils.findMethod(DateTimeTools.class, "saveActorsFilmsToDatabase", ActorsFilms.class);
        String jsonSchema = JsonSchemaGenerator.generateForMethodInput(method);
        System.out.println(jsonSchema);
    }


    /**
     * 更新用户信息
     * 使用{@link ToolParam}指定参数为非必须参数
     * {@link DateTimeTools#updateCustomerInfo(Long, String, String)}
     */
    @Test
    void generateJsonSchemaForFunction() {
        var content = ChatClient.create(chatModel)
                .prompt()
                .user("Update the customer information for customer with id 12345. his name is james")
                .tools(new DateTimeTools())
                .call()
                .content();
        System.out.println(content);
    }

}
