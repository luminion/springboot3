package org.example.springboot10reactor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * web flux主要应用
 * 对于响应式编程，组件和传统阻塞式有所不用
 * | API功能    | **Servlet-阻塞式Web**                     | **WebFlux-响应式Web**                                             |
 * |:-------- | -------------------------------------- | -------------------------------------------------------------- |
 * | 前端控制器    | DispatcherServlet                      | DispatcherHandler                                              |
 * | 处理器      | Controller                             | WebHandler/Controller                                          |
 * | 请求、响应    | **ServletRequest**、**ServletResponse** | **ServerWebExchange：****ServerHttpRequest、ServerHttpResponse** |
 * | 过滤器      | Filter（HttpFilter）                     | WebFilter                                                      |
 * | 异常处理器    | HandlerExceptionResolver               | DispatchExceptionHandler                                       |
 * | Web配置    | @EnableWebMvc                          | @EnableWebFlux                                                 |
 * | 自定义配置    | WebMvcConfigurer                       | WebFluxConfigurer                                              |
 * | 返回结果     | 任意                                     | **Mono、Flux**、任意                                               |
 * | 发送REST请求 | RestTemplate                           | WebClient                                                      |
 *
 * @author booty
 */
@SpringBootApplication
public class WebFluxMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebFluxMainApplication.class,args);
    }
}
