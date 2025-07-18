package com.example.webclient.service;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;

/**
 * 气象接口
 *
 * @author booty
 */

public interface WeatherApi {


    /**
     * 查询天气
     * 此处使用@RequestParam注解表示对外调用接口时传递的参数名
     * 例如:
     * 当@RequestParam指定为area时，对外调用接口时传递出去的是area参数
     * 当@RequestParam指定为city时，对外调用接口时传递出去的是city参数
     * @param city 城市
     * @return {@code Mono<String> }
     * @author booty
     */
    @GetExchange(url = "https://ali-weather.showapi.com/area-to-weather-date",accept = "application/json")
    Mono<String> queryWeather(@RequestParam("area") String city);
}
