package com.example.webclient;

import com.example.webclient.service.ExpressApi;
import com.example.webclient.service.WeatherApi;
import com.example.webclient.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebclientAppTests {

    @Test
    void contextLoads() {
    }


    @Autowired
    ExpressApi expressApi;
    @Autowired
    WeatherApi weatherApi;
    @Autowired
    WeatherService weatherService;


    @Test
    public void testWeather(){
        String s = weatherService.weather("北京").block();
        System.out.println(s);

    }



}
