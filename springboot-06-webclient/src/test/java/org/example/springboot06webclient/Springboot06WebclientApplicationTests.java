package org.example.springboot06webclient;

import org.example.springboot06webclient.service.ExpressApi;
import org.example.springboot06webclient.service.WeatherApi;
import org.example.springboot06webclient.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Springboot06WebclientApplicationTests {

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
