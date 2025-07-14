package com.example.springboot21ai.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.function.Function;

/**
 * 获取天气的函数
 *
 * @author bootystar
 */
@Slf4j
public class WeatherService implements Function<WeatherService.WeatherRequest, WeatherService.WeatherResponse> {

    public WeatherResponse apply(WeatherRequest request) {
        log.info("\n---- function : WeatherFunction was called ----, request: {}", request);
        return new WeatherResponse(30.0, Unit.C);
    }

    public enum Unit { C, F }
    public record WeatherRequest(@ToolParam(description = "The name of a city or a country") String location, Unit unit) {}
    public record WeatherResponse(double temp, Unit unit) {}


}

