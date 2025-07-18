package com.example.ai.tool;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.function.Function;

/**
 * 获取天气的函数
 *
 * @author bootystar
 */
@Slf4j
public class WeatherService implements Function<WeatherService.WeatherRequest,WeatherService.WeatherResponse> {

    public WeatherResponse apply(WeatherRequest weatherRequest) {
        log.info("\n---- function : WeatherFunction was called ----, request: {}", weatherRequest);
        double temperature = 5.0;
        var location = weatherRequest.location();
        switch (location){
            case "Beijing" -> temperature = 10.0;
            case "Shanghai" -> temperature = 15.0;
            case "Guangzhou" -> temperature = 20.0;
            case "Shenzhen" -> temperature = 25.0;
            case "Hangzhou" -> temperature = 30.0;
            case "Chengdu" -> temperature = 35.0;
            case "Paris" -> temperature = 40.0;
            case "Tokyo" -> temperature = 45.0;
            case "Francisco" -> temperature = 50.0;
        }
        return new WeatherResponse(temperature, weatherRequest.unit());
    }

    public enum Unit {C, F}
    public record WeatherRequest(@ToolParam(description = "The name of a city or a country") String location, Unit unit) {}
    public record WeatherResponse(double temp, Unit unit) {}


}

