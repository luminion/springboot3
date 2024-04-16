package org.example.springboot11webflux.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Mono和Flux的创建
 * spring中reactor中的流有2种,
 * Flux: 0-N个元素
 * Mono: 0-1个元素
 * flux和mono创建后, 在被订阅之前, 不会有任何操作
 * 仅仅是定义了流的操作, 只有在订阅时, 才会触发这些操作
 *
 * 文档地址:
 * https://projectreactor.io/docs/core/release/reference/
 * @author booty
 */
public class T02FluxAndMono {

    /**
     * 创建mono
     *
     * @author booty
     */
    @Test
    void createMono() {
        // mono的创建
        System.out.println("=================Mono==================");
        Mono<Integer> mono = Mono.just(1);
        mono.subscribe(System.out::println);
    }

    /**
     * 创建Flux
     *
     * @author booty
     */
    @Test
    void createFlux() {
        // 通过给定元素创建
        Flux<String> just = Flux.just("item1", "item2", "item3");
        just.subscribe(System.out::println);
    }

    /**
     * 指定范围创建Flux
     *
     * @author booty
     */
    @Test
    void createFluxFromRange() {
        // 通过给定范围创建, 包含1~5的数字
        Flux<Integer> range = Flux.range(1, 5);
        range.subscribe(System.out::println);
    }

    /**
     * 通过集合创建Flux
     *
     * @author booty
     */
    @Test
    void createFluxFromIterable() {
        // 通过给定集合创建
        List<String> iterable = Arrays.asList("foo", "bar", "foobar");
        Flux<String> fromIterable = Flux.fromIterable(iterable);
        fromIterable.subscribe(System.out::println);
    }

    /**
     * 按照指定时间间隔创建元素
     *
     * @author booty
     */
    @Test
    void createFluxWithInterval() throws Exception {
        //每秒产生一个从0开始的递增数字
        Flux<Long> flux = Flux.interval(Duration.ofSeconds(1));
        flux.subscribe(System.out::println);

        // flux是异步的, 此处需要保持主线程不退出
        Thread.sleep(1000*10);
    }


    /**
     * 日志操作
     *
     * @author booty
     */
    @Test
    void logOperation() throws Exception {
        // mono和flux的日志操作
        Flux<Integer> range = Flux.range(1, 7);
        range.filter(i -> i > 3) //挑出>3的元素
                .delayElements(Duration.ofSeconds(1)) //获取元素后延迟1秒再获取下一个元素
                .log()  //日志打印, 可以重复添加,根据不同位置打印不同内容, 目前打印4-7;添加到filter之前, 则会打印1-7, 添加到map后,则会打印转化为字符串后的内容
                .map(i -> "Str-" + i) // 在数字的前方添加字母将其转为字符串
                .subscribe(System.out::println); //订阅, 打印出每个元素
        // 一个流的所有操作都在subscribe时发生, 若没有subscribe, 则不会有任何操作

        // flux是异步的, 此处需要保持主线程不退出
        // flux是异步的, 此处需要保持主线程不退出
        Thread.sleep(1000*10);
    }


    /**
     * 将元素按照指定的条件分组为不同list
     *
     *
     * @author booty
     */
    @Test
    void bufferUntilChanged() throws Exception {
        // 缓冲操作
        Flux.range(1,10)
                .bufferUntilChanged(e->e%3==0)// 按照是否能被3整除, 拆分, 会按顺序指定,遇到满足条件的放一组,遇到不同条件放另一组
                .subscribe(System.out::println);
        /*
        [1, 2]
        [3]
        [4, 5]
        [6]
        [7, 8]
        [9]
        [10]
         */
    }




}
