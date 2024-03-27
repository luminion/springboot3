package org.example.springboot10reactor;

import org.example.springboot10reactor.base.SampleSubscriberOnHook;
import org.example.springboot10reactor.base.SampleSubscriber;
import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * Mono和Flux的订阅(消费)
 * 文档地址:
 * https://projectreactor.io/docs/core/release/reference/
 *
 * @author booty
 */
public class T03Subscribe {

    /**
     * 订阅时, 可以添加消费者参数, 感知流的运行及结束
     * 同时处理时可以调用订阅后对象的dispose()方法取消订阅
     * dispose()方法不是即时的,程序生产速度快时, 可能在收到指令前继续生产多个元素
     * 方法签名:
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
     * 参数1,流中每个元素处理完成时调用的消费者,
     * 参数2,流中出现异常时调用的消费者,
     * 参数3,流正常结束时调用的消费者,
     * 参数4,订阅时调用的消费者(可以控制背压)
     *
     * @author booty
     */
    @Test
    void subscribeWithLambda() throws Exception {
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
     * 通过订阅器处理事件
     * reactor.core.publisher.BaseSubscriber
     * 如果在订阅流时, 不希望使用lambdas表达感知doOnXxx的写法, 可以在订阅时传入一个通用订阅者来进行操作
     * 通过编写一个继承抽象类BaseSubscriber的订阅者来进行操作
     * BaseSubscriber还提供了一个requestUnbounded()方法来切换到无界模式 （相当于request(Long.MAX_VALUE)），以及一个cancel()方法。
     * 并针对doOnXxx事件提供了对应的hookOnXxx方法供子类重写，用于在相应的事件发生时执行相应的操作。
     * @author booty
     */
    @Test
    void subscribeWithBaseSubscriber() throws Exception {
        // 继承BaseSubscriber的简单订阅者
        SampleSubscriber<Integer> subscriber = new SampleSubscriber<>();
        Flux<Integer> flux = Flux.range(1, 4);
        flux.subscribe(subscriber);
    }











}
