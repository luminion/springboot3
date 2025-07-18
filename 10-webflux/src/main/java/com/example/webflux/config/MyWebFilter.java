package com.example.webflux.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFilter 相当于MVC中的Interceptor
 * 用于拦截请求,
 * 与Interceptor的区别,
 * WebFilter注入到容器中即可, 不需要配置
 * interceptor需要配置
 * @author bootystar
 */
@Component
public class MyWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        System.out.println("请求处理放行到目标方法之前...");
        Mono<Void> filter = chain.filter(exchange); //放行


        // 注意, 流一旦经过某个操作就会变成新流
        Mono<Void> voidMono = filter.doOnError(err -> {
                    System.out.println("目标方法异常以后...");
                }) // 目标方法发生异常后做事
                .doFinally(signalType -> {
                    System.out.println("目标方法执行以后...");
                });// 目标方法执行之后
        //上面执行不花时间。

        return voidMono; //看清楚返回的是谁 ！！！
    }
}
