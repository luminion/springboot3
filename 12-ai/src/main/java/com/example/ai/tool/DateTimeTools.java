package com.example.ai.tool;

import com.example.ai.entity.ActorsFilms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 注解式工具
 * 使用{@link Tool}注解声明方法
 * <p>
 * 注解属性:
 * <p>
 * name :
 * 工具的名称。如果未提供，将使用方法名
 * <p>
 * description :
 * 工具的描述，模型可以使用此描述来理解何时以及如何调用工具。
 * 如果未提供，将使用方法名作为工具描述。
 * 然而，强烈建议提供详细描述，因为这对于模型理解工具的用途和如何使用它至关重要
 * <p>
 * returnDirect :
 * 工具结果是否应直接返回给客户端或传递回模型。详见“直接返回”部分
 * <p>
 * resultConverter :
 * 用于将工具调用的结果转换为 String object 以发送回 AI 模型的 ToolCallResultConverter 实现方式。详见“结果转换”部分
 * <p>
 * 该注解标注的方法可以是静态的或实例的，它可以具有任何可见性（公共、受保护、包私有或私有）。
 * 包含该方法的类可以是顶层类或嵌套类，它也可以具有任何可见性（只要它在您计划实例化的地方是可访问的）
 * <p>
 * <p>
 * 您可以为方法定义任意数量的参数（包括无参数），并且大多数类型都可以使用（原始类型、POJO、枚举、列表、数组、映射等）。
 * 类似地，方法可以返回大多数类型，包括 void。
 * 如果方法返回值，返回类型必须是一个可序列化类型，因为结果将被序列化并发送回模型
 * 以下类型目前不能作为工具方法使用的参数或返回类型
 * Optional
 * 异步类型( CompletableFuture 、 Future ),
 * 响应式类型（例如 Flow 、 Mono 、 Flux ）
 * 函数式类型（例如 Function 、 Supplier 、 Consumer ）
 *
 * @author bootystar
 */
@Slf4j
public class DateTimeTools {

    /**
     * 获取当前日期时间的工具
     * <p>
     * 使用{@link Tool}注解声明方法, 该注解有以下属性:
     * <p>
     * name : 工具的名称。非必须，如果未提供，将使用方法名
     * <p>
     * description : 工具的描述，模型可以使用此描述来理解何时以及如何调用工具。如果未提供，将使用方法名作为工具描述。然而，强烈建议提供详细描述，因为这对于模型理解工具的用途和如何使用它至关重要
     * <p>
     * returnDirect : 工具结果是否应直接返回给客户端或传递回模型。详见“直接返回”部分
     * <p>
     * resultConverter :用于将工具调用的结果转换为 String object 以发送回 AI 模型的实现方式。详见“结果转换”部分
     *
     * @return {@link String }
     */
    @Tool(description = "Get the current date and time in the user's timezone") // 工具描述
    public String getCurrentDateTime() {
        log.info("\n---- getCurrentDateTime tool was called ----");
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }


    /**
     * 对指定的时间设置闹钟
     *
     * @param time 时间
     */
    @Tool(description = "Set a user alarm for the given time, provided in ISO-8601 format")
    public void setAlarm(String time) {
        log.info("\n---- setAlarm tool was called ----time: {}", time);
        LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        System.out.println("Alarm set for " + alarmTime);
    }

    /**
     * 对执行的日期设置待办事项
     * <p>
     * <p>
     * 通过{@link @ToolParam}指定参数的描述, 该注解有以下属性:
     * <p>
     * description : 参数的描述，模型可以使用它来更好地理解如何使用该参数
     * <p>
     * required : 参数是必需的还是可选的。默认情况下，所有参数都被视为必需的
     *
     * @return {@link String }
     */
    @Tool(description = "Set a user todo reminder for the given date")
    public String setTodoRemind(@ToolParam(description = "Time in ISO-8601 format") String time) {
        log.info("\n---- setTodoRemind tool was called ----, time: {}", time);
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }




}