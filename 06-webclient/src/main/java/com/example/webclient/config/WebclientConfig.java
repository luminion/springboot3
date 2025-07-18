package com.example.webclient.config;

import com.example.webclient.service.ExpressApi;
import com.example.webclient.service.WeatherApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * webclient配置
 *
 * @author booty
 */
@Configuration
public class WebclientConfig {

    /**
     * http服务代理工厂
     *
     * @param appCode 阿里云查询服务的code
     * @return {@code HttpServiceProxyFactory }
     * @author booty
     */
    @Bean
    public HttpServiceProxyFactory httpServiceProxyFactory(@Value("${aliyun.appcode}") String appCode){
        //1、创建客户端
        WebClient client = WebClient.builder()
                .defaultHeader("Authorization","APPCODE "+appCode) //添加默认请求头
                .codecs(clientCodecConfigurer -> {
                    clientCodecConfigurer
                            .defaultCodecs()
                            .maxInMemorySize(256*1024*1024);
                    //响应数据量太大有可能会超出BufferSize，所以这里设置的大一点
                }) // 编码格式
                .build();
        //2、创建代理工厂
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder()
                .exchangeAdapter(WebClientAdapter.create(client))
                .build();
        return factory;
    }

    @Bean
    public WeatherApi weatherInterface(HttpServiceProxyFactory httpServiceProxyFactory){
        //3、获取代理对象(通过代理工厂创建后, 发送请求会自动带上请求头)
        WeatherApi weatherInterface = httpServiceProxyFactory.createClient(WeatherApi.class);
        return weatherInterface;
    }

    @Bean
    public ExpressApi expressApi(HttpServiceProxyFactory httpServiceProxyFactory){
        //3、获取代理对象(通过代理工厂创建后, 发送请求会自动带上请求头)
        ExpressApi client = httpServiceProxyFactory.createClient(ExpressApi.class);
        return client;
    }
}
