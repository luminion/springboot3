package com.example.webclient.service;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;

/**
 * express api
 *
 * @author booty
 */
public interface ExpressApi {

    /**
     * 查询快递物流信息
     * 此处使用@RequestParam注解表示对外调用接口时传递的参数名
     * 例如:
     * 当@RequestParam指定为number时，对外调用接口时传递出去的是number参数
     * 当@RequestParam指定为serial时，对外调用接口时传递出去的是serial参数
     *
     * @param number 快递单号
     * @param type   快递类型，不传将自动识别
     * @param mobile 查顺丰时需要手机号后四位
     * @return {@code Mono<String> }
     * @author booty
     */
    @GetExchange(url = "https://express3.market.alicloudapi.com/express3",accept = "application/json")
    Mono<String> queryExpress(@RequestParam("number") String number, @RequestParam("type") String type, @RequestParam("mobile") String mobile);
}
