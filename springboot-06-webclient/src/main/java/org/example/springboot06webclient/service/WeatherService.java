package org.example.springboot06webclient.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 气象服务
 *
 * @author booty
 */
@Service
public class WeatherService {


    public Mono<String> weather(String city){

        //1、创建WebClient
        WebClient client = WebClient.create();
        //2、准备数据
        Map<String,String> params = new HashMap<>();
        params.put("area", city);
        //3、定义发请求行为  mono为异步请求的包装,可以暂时将其当做多线程任务中的CompletableFuture看待
        Mono<String> mono = client.get()
                .uri("https://ali-weather.showapi.com/area-to-weather-date?area={area}", params)
                .accept(MediaType.APPLICATION_JSON) //定义响应的内容类型
                .header("Authorization", "APPCODE 93b7e19861a24c519a7548b17dc16d75") //定义请求头
                .retrieve()
                .bodyToMono(String.class);
        return mono;
    }

}
