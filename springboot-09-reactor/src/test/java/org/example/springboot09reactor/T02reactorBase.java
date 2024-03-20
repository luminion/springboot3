package org.example.springboot09reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class T02reactorBase {

    /*
    spring中reactor中的流有2种,
    Flux: 0-N个元素

    Mono: 0-1个元素

    flux和mono创建后, 在被订阅之前, 不会有任何操作
    仅仅是定义了流的操作, 只有在订阅时, 才会触发这些操作
     */

    @Test
    void test1() throws Exception {
        // mono和flux的创建及使用
        Mono<Integer> just = Mono.just(1);
        just.subscribe(System.out::println);

        System.out.println("===================================");
        // 创建一个Flux, 包含1~5的数字
        Flux<Integer> just1 = Flux.range(1, 5);
        just1.subscribe(System.out::println);

        System.out.println("===================================");
        //每秒产生一个从0开始的递增数字
        Flux<Long> flux = Flux.interval(Duration.ofSeconds(1));
        flux.subscribe(System.out::println);

        // flux是异步的, 此处需要保持主线程不退出
        System.in.read();

    }


    @Test
    void test2() throws Exception {
        // mono和flux的日志操作
        Flux<Integer> range = Flux.range(1, 7);
        range.filter(i -> i > 3) //挑出>3的元素
                .delayElements(Duration.ofSeconds(1)) //获取元素后延迟1秒再获取下一个元素
                .log()  //日志打印, 可以重复添加,根据不同位置打印不同内容, 目前打印4-7;添加到filter之前, 则会打印1-7, 添加到map后,则会打印转化为字符串后的内容
                .map(i -> "Str-" + i) // 在数字的前方添加字母将其转为字符串
                .subscribe(System.out::println); //订阅, 打印出每个元素
        // 一个流的所有操作都在subscribe时发生, 若没有subscribe, 则不会有任何操作

        // flux是异步的, 此处需要保持主线程不退出
        System.in.read();
    }




}
