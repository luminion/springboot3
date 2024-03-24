package org.example.springboot10reactor;

import org.example.springboot10reactor.base.SampleSubscriber;
import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * 文档地址:
 * https://projectreactor.io/docs/core/release/reference/
 *
 *
 * @author booty
 */
public class T02MonoAndFlux {

    /**
     * spring中reactor中的流有2种,
     * Flux: 0-N个元素
     * Mono: 0-1个元素
     * flux和mono创建后, 在被订阅之前, 不会有任何操作
     * 仅仅是定义了流的操作, 只有在订阅时, 才会触发这些操作
     *
     * @author booty
     */
    @Test
    void test1() throws Exception {
        // mono的创建
        System.out.println("=================Mono==================");
        Mono<Integer> mono = Mono.just(1);
        mono.subscribe(System.out::println);

        System.out.println("=================Flux==================");

        // 通过给定元素创建
        Flux<String> just = Flux.just("item1", "item2", "item3");
        just.subscribe(System.out::println);

        // 通过给定范围创建, 包含1~5的数字
        Flux<Integer> range = Flux.range(1, 5);
        range.subscribe(System.out::println);

        // 通过给定集合创建
        List<String> iterable = Arrays.asList("foo", "bar", "foobar");
        Flux<String> fromIterable = Flux.fromIterable(iterable);
        fromIterable.subscribe(System.out::println);

        //每秒产生一个从0开始的递增数字
        Flux<Long> flux = Flux.interval(Duration.ofSeconds(1));
        flux.subscribe(System.out::println);

        // flux是异步的, 此处需要保持主线程不退出
        Thread.sleep(1000*10);
    }


    /**
     * 订阅时, 可以添加消费者参数, 感知流的运行及结束
     * 同时处理时可以调用订阅后对象的dispose()方法取消订阅
     * dispose()方法不是即时的,程序生产速度快时, 可能在收到指令前继续生产多个元素
     * 参数1,流中每个元素处理完成时调用的消费者,
     * 参数2,流中出现异常时调用的消费者,
     * 参数3,流正常结束时调用的消费者,
     * 参数4,订阅时调用的消费者(可以控制背压)
     * subscribe();
     * subscribe(Consumer<? super T> consumer);
     * subscribe(Consumer<? super T> consumer,
     *         Consumer<? super Throwable> errorConsumer);
     * subscribe(Consumer<? super T> consumer,
     *         Consumer<? super Throwable> errorConsumer,
     *         Runnable completeConsumer);
     * subscribe(Consumer<? super T> consumer,
     *         Consumer<? super Throwable> errorConsumer,
     *         Runnable completeConsumer,
     *         Consumer<? super Subscription> subscriptionConsumer)
     *
     * @author booty
     */
    @Test
    void test2() throws Exception {
        // mono和flux的订阅和结束
        Flux<Integer> range = Flux.range(1, 9);
        Disposable subscribe = range
                .delayElements(Duration.ofSeconds(1)) //获取元素后延迟1秒再获取下一个元素
                .map(e->e+"-handle") // 在数字的后方添加字母将其转为字符串
                .subscribe(
                    element -> System.out.println("element = " + element), // 感知元素被消费(所有中间操作处理完成后调用)
                    throwable -> System.out.println("throwable = " + throwable), //感知异常
                    () -> System.out.println("流结束了...") //感知正常结束
        );
        Thread.sleep(1000*5);
        // 手动取消订阅,
        subscribe.dispose();

        // flux是异步的, 此处需要保持主线程不退出
        Thread.sleep(1000*10);
    }


    /**
     * reactor.core.publisher.BaseSubscriber
     * 如果在订阅流时, 不希望使用lambdas的写法, 可以在订阅时传入一个通用订阅者来进行操作
     * 可以通过编写一个继承抽象类BaseSubscriber的订阅者来进行操作
     * BaseSubscriber还提供了一个requestUnbounded()方法来切换到无界模式 （相当于request(Long.MAX_VALUE)），以及一个cancel()方法。
     * @author booty
     */
    @Test
    void test3() throws Exception {
        // 继承BaseSubscriber的简单订阅者
        SampleSubscriber<Integer> ss = new SampleSubscriber<Integer>();
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(ss);
    }


    @Test
    void test11() throws Exception {
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




}
