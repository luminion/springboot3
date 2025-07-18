package com.example.webflux.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


/**
 * Webflux中Controller自动注入的参数类型
 * https://docs.spring.io/spring-framework/reference/6.0/web/webflux/controller/ann-methods/arguments.html
 *
 | ServerWebExchange                                                                 | 封装了请求和响应对象的对象; 自定义获取数据、自定义响应                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
 | --------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
 | ServerHttpRequest, ServerHttpResponse                                             | 请求、响应                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
 | WebSession                                                                        | 访问Session对象                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
 | java.security.Principal                                                           | <br>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
 | org.springframework.http.HttpMethod                                               | 请求方式                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
 | java.util.Locale                                                                  | 国际化                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
 | java.util.TimeZone + java.time.ZoneId                                             | 时区                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
 | @PathVariable                                                                     | 路径变量                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
 | @MatrixVariable                                                                   | 矩阵变量                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
 | @RequestParam                                                                     | 请求参数                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
 | @RequestHeader                                                                    | 请求头；                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
 | @CookieValue                                                                      | 获取Cookie                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
 | @RequestBody                                                                      | 获取请求体，Post、文件上传                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
 | HttpEntity<B>                                                                     | 封装后的请求对象                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
 | @RequestPart                                                                      | 获取文件上传的数据 multipart/form-data.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
 | java.util.Map, org.springframework.ui.Model, and org.springframework.ui.ModelMap. | Map、Model、ModelMap                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
 | @ModelAttribute                                                                   | <br>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
 | Errors, BindingResult                                                             | 数据校验，封装错误                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
 | SessionStatus + class-level @SessionAttributes                                    |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
 | UriComponentsBuilder                                                              | For preparing a URL relative to the current request’s host, port, scheme, and context path. See [URI Links](https://docs.spring.io/spring-framework/reference/6.0/web/webflux/uri-building.html).                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
 | @SessionAttribute                                                                 | <br>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
 | @RequestAttribute                                                                 | 转发请求的请求域数据                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
 | Any other argument                                                                | 所有对象都能作为参数：1、基本类型 ，等于标注@RequestParam 2、对象类型，等于标注 @ModelAttribute目标方法传参<br/>https://docs.spring.io/spring-framework/reference/6.0/web/webflux/controller/ann-methods/arguments.html<br/>Controller method argument	Description<br/>ServerWebExchange	封装了请求和响应对象的对象; 自定义获取数据、自定义响应<br/>ServerHttpRequest, ServerHttpResponse	请求、响应<br/>WebSession	访问Session对象<br/>java.security.Principal	<br/>org.springframework.http.HttpMethod	请求方式<br/>java.util.Locale	国际化<br/>java.util.TimeZone + java.time.ZoneId	时区<br/>@PathVariable	路径变量<br/>@MatrixVariable	矩阵变量<br/>@RequestParam	请求参数<br/>@RequestHeader	请求头；<br/>@CookieValue	获取Cookie<br/>@RequestBody	获取请求体，Post、文件上传<br/>HttpEntity<B>	封装后的请求对象<br/>@RequestPart	获取文件上传的数据 multipart/form-data.<br/>java.util.Map, org.springframework.ui.Model, and org.springframework.ui.ModelMap.	Map、Model、ModelMap<br/>@ModelAttribute	<br/>Errors, BindingResult	数据校验，封装错误<br/>SessionStatus + class-level @SessionAttributes	<br/>UriComponentsBuilder	For preparing a URL relative to the current request’s host, port, scheme, and context path. See URI Links.<br/>@SessionAttribute	<br/>@RequestAttribute	转发请求的请求域数据<br/>Any other argument	所有对象都能作为参数：<br/>1、基本类型 ，等于标注@RequestParam <br/>2、对象类型，等于标注 @ModelAttribute |

 *
 * @author bootystar
 */
@RestController
public class ReactorController {


    /**
     * WebFlux：向下兼容原来SpringMVC的大多数注解和API；
     * 不过请求和相应不再是Servlet的request和response，而是ServerHttpRequest和ServerHttpResponse
     *
     * @param key           指定的请求参数
     * @param exchange      提供了对请求和响应对象的封装
     * @param webSession    web会话
     * @param httpMethod    请求方式
     * @return {@link String }
     * @author bootystar
     */
    @PostMapping("/test")
    public String test(@RequestParam(value = "key",required = false,defaultValue = "哈哈") String key
            , ServerWebExchange exchange
            , WebSession webSession
            , HttpMethod httpMethod
    ){
        // 获取请求和响应对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 获取session对象
        WebSession session = webSession;
        System.out.printf("请求方法:%s,\r\n",httpMethod);
        System.out.printf("请求Entity:%s,\r\n",request.getMethod());
        return "Hello World!!! key="+key;
    }

    /**
     * 请求的实体
     *
     * @param entity 封装了整个请求的信息
     * @return {@link Mono }<{@link ? }>
     * @author bootystar
     */
    @PostMapping("/entity")
    public Mono<?> entity(HttpEntity<String> entity){
        return Mono.just(entity.getHeaders());
    }

    /**
     * 请求体
     *
     * @param body 车身
     * @return {@link Mono }<{@link String }>
     * @author bootystar
     */
    @PostMapping("/body")
    public Mono<String> body(@RequestBody String body){
        return Mono.just(body);
    }




    /**
     * 文件上传
     *
     * @param file 文件,相当于原来的MultipartFile
     * @return {@link Mono }<{@link ? }>
     * @author bootystar
     */
    @PostMapping("/file")
    public Mono<?> file(FilePart file){
        return Mono.just(file.filename());
    }





    /**
     * 虽然Webflux支持原来的大多类型, 但更推荐使用Mono返回单个数据
     * 返回单个数据Mono： Mono<Order>、User、String、Map
     *
     * @return {@link Mono }<{@link ? }>
     * @author bootystar
     */
    @GetMapping("/mono")
    public Mono<?> mono(){
        return Mono.just(0)
                .map(i-> 10/i)
                .map(i->"哈哈-"+i);
    }

    /**
     * 虽然Webflux支持原来的大多类型, 但更推荐使用Flux返回多个数据
     *
     * @return {@link Flux }<{@link ? }>
     * @author bootystar
     */
    @GetMapping("/flux")
    public Flux<?> flux(){
        return Flux.range(0,10);
    }

    /**
     * SSE
     * 即ServerSentEvent,表示服务器主动向客户端发送消息, 客户端接收形式为text/event-stream
     * 目前主流的ai对话就是使用sse达成的
     *
     * sse和websocket区别：
     * ● SSE：单工；请求过去以后，等待服务端源源不断的数据
     * ● websocket：双工： 连接建立后，可以任何交互；
     *
     * 使用static/index.html文件结合该接口就能达到类似ai回答问题的效果
     *
     *
     * @return {@link Flux }<{@link ServerSentEvent }<{@link String }>>
     * @author bootystar
     */
    @GetMapping(value = "/sse",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> sse(){
        String text ="Server-Sent Events (SSE) 是一种基于 HTTP 的技术，它允许服务器端数据自动推送到客户端，而无需客户端发起请求。这是 HTML5 规范的一部分，为网页提供了从服务器获取实时更新的能力。SSE 主要用于实现如下功能：\n" +
                "\n" +
                "实时通知：如股票报价、新闻更新、即时消息等。\n" +
                "实时数据更新：体育比赛分数、天气更新、传感器数据等。\n" +
                "实时监控：服务器性能监控、日志监控等。\n" +
                "技术特点：\n" +
                "单向通信：SSE 提供了一种从服务器到客户端的单向消息传递机制。\n" +
                "持久连接：建立一个长连接，服务器可以随着时间推移发送多个事件，而连接保持开放状态。\n" +
                "轻量级协议：数据通过 text/event-stream MIME 类型格式传输，易于解析。\n" +
                "自动重新连接：如果连接中断，浏览器会自动尝试重新建立连接（除非显式关闭）。\n" +
                "事件驱动：在客户端，可以通过 JavaScript 的 EventSource 对象监听特定事件，处理服务器推送的数据。\n" +
                "跨域支持：支持 CORS（跨源资源共享），使得跨域推送成为可能。\n" +
                "工作原理简述：\n" +
                "服务器端设置响应头 Content-Type 为 text/event-stream，然后以明文格式发送一系列事件数据。\n" +
                "每个事件包括但不限于：data（实际数据）、event（事件类型，可选）、id（事件ID，用于重新连接时识别已接收的事件，可选）等字段。\n" +
                "客户端使用 JavaScript 的 EventSource 对象创建一个到服务器的新连接，并通过事件监听器（如 onmessage、onopen、onerror）处理服务器推送的信息";
        char[] charArray = text.toCharArray();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < charArray.length; i++) {
            list.add(String.valueOf(charArray[i]));
        }
        return Flux.fromIterable(list)
                .map(data-> {
                    //构建一个SSE对象
                   return    ServerSentEvent
                            .builder(data)
                            .build();
                })
                .delayElements(Duration.ofMillis(10));
    }


}
