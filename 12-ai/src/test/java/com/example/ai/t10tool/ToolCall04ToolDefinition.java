package com.example.ai.t10tool;

import com.example.ai.tool.DateTimeTools;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.support.ToolDefinitions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 工具定义
 * {@link ToolDefinition}
 * 接口提供了 AI 模型所需的信息，以了解工具的可用性，包括工具名称、描述和输入模式。
 * 实际每个提供给大模型的ToolCall都被封装为ToolCallback的实现, 而每个ToolCallback都必须提供一个 ToolDefinition提供工具的信息
 *
 * @author bootystar
 */
@SpringBootTest
public class ToolCall04ToolDefinition {

    @Autowired
    private ChatModel chatModel;

    /**
     * 通过builder手动创建ToolDefinition
     * 该接口具有3个属性:
     * name : 工具名称
     * description : 工具描述
     * inputSchema : 输入参数描述, 支持JSON String, Number, Array, Object or token 'null', 'true' or 'false'
     */
    @Test
    @SneakyThrows
    void createToolDefinitionByBuilder() {
        ToolDefinition toolDefinition = ToolDefinition.builder()
                .name("currentWeather")
                .description("Get the weather in location")
                .inputSchema("""
                            {
                                "type": "object",
                                "properties": {
                                    "location": {
                                        "type": "string"
                                    },
                                    "unit": {
                                        "type": "string",
                                        "enum": ["C", "F"]
                                    }
                                },
                                "required": ["location", "unit"]
                            }
                        """)
                .build();
    }

    /**
     * 通过ToolDefinitions直接生成方法对应的ToolDefinition
     * <p>
     * 从方法生成的 ToolDefinition 包括方法名作为工具名、方法名作为工具描述，以及方法输入参数的 JSON schema。
     * 如果方法使用了 @Tool 注解，并且设置了工具名和描述，那么工具名和描述将取自注解
     */
    @Test
    void methodToolDefinition() {
        Method method = ReflectionUtils.findMethod(DateTimeTools.class, "getCurrentDateTime");
        // 从方法生成 ToolDefinition
        ToolDefinition toolDefinition = ToolDefinitions.from(method);
    }

    /**
     * 通过ToolDefinitions生成方法对应的ToolDefinition.builder,在此基础上配置后再生成ToolDefinition
     */
    @Test
    void methodToolDefinitionBuilder() {
        Method method = ReflectionUtils.findMethod(DateTimeTools.class, "getCurrentDateTime");
        // 从方法生成 ToolDefinition，的builder, 并更改属性后生成ToolDefinition
        ToolDefinition toolDefinition = ToolDefinitions.builder(method)
                .name("currentTime") // 更改工具名称
                .build();
    }

    /**
     * Function Tool Definition
     * 函数式工具的 ToolDefinition 可以通过 FunctionToolCallback 获取
     */
    @Test
    void functionToolDefinition() {
        // 创建函数式工具
        var functionToolCallback = FunctionToolCallback
                .builder("getCurrentTimeMillis", System::currentTimeMillis)
                .description("Get the current Timestamp in the user's timezone")
                .build();
        // 从函数式工具获取 ToolDefinition
        var toolDefinition = functionToolCallback.getToolDefinition();
    }

   
 

}
