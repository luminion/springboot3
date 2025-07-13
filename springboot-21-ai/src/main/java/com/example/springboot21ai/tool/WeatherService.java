package com.example.springboot21ai.tool;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

/**
 * 天气服务
 *
 * @author bootystar
 */
@Slf4j
public class WeatherService implements Function<WeatherService.WeatherRequest, WeatherService.WeatherResponse> {
    public WeatherResponse apply(WeatherRequest request) {
        log.info("\n---- WeatherService function was called ----, request: {}", request);
        return new WeatherResponse(30.0, Unit.C);
    }

    public enum Unit { C, F }
    public record WeatherRequest(String location, Unit unit) {}
    public record WeatherResponse(double temp, Unit unit) {}


}

