package com.example.webclient.controller;

import com.example.webclient.service.ExpressApi;
import com.example.webclient.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


/**
 * 测试控制器
 *
 * @author booty
 */
@RestController
public class TestController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private ExpressApi expressApi;

    /**
     * 查询天气
     * @param city 城市
     * @return {@code Mono<String> }
     * @author booty
     */
    @GetMapping("/weather")
    public Mono<String> weather(@RequestParam("city") String city){
        return weatherService.weather(city);
    }

    /**
     * 查询物流
     * @param number 快递单号
     * @return {@code Mono<String> }
     * @author booty
     */
    @GetMapping("/express")
    public Mono<String> express(@RequestParam("number") String number){
        //获取物流
        return expressApi.queryExpress(number, null, null);
    }
}
