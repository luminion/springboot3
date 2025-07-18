package com.example.webflux;

import com.example.webflux.clazz.SampleSubscriberOnHook;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class T03SignalType {



    /**
     * 事件感知:
     * 在Flux/Mono/订阅后返回的Disposable对象中提供了一系列基于事件感知的api
     * 操作者可以通过这些api感知事件并执行相应操作
     *
     * 其中有2个关键
     * SignalType：(触发信号)
     *   SUBSCRIBE          被订阅
     *   REQUEST            请求了N个元素
     *   CANCEL             流被取消
     *   ON_SUBSCRIBE       在订阅时候
     *   ON_NEXT            在元素到达
     *   ON_ERROR           在流错误
     *   ON_COMPLETE        在流正常完成时
     *   AFTER_TERMINATE    中断以后
     *   CURRENT_CONTEXT    当前上下文
     *   ON_CONTEXT         感知上下文
     * doOnXxx (API触发时机)
     *   1、doOnNext         每个数据（流的数据）到达的时候触发
     *   2、doOnEach         每个元素（流的数据和信号）到达的时候触发
     *   3、doOnRequest      消费者请求流元素的时候
     *   4、doOnError        流发生错误
     *   5、doOnSubscribe    流被订阅的时候
     *   6、doOnTerminate    发送取消/异常信号中断了流
     *   7、doOnCancel       流被取消
     *   8、doOnDiscard      流中元素被忽略的时候
     * @author bootystar
     */
    @Test
    void differenceBetweenDoOnNextAndDoOnEach() {
        // doOnNext和doOnEach的区别
        // doOnNext：每个数据（流的数据）到达的时候触发,只能接收到数据
        // doOnEach：每个元素（流的数据和信号）到达的时候触发, 同时可以接收到数据和信号
        // 同时, doOnXxx要感知某个流的事件，写在这个流的后面，新流的前面,
        // 例如下面的filter之后, 便产生了一个新流, 第二个doOnNext就不会感知之前流的动作,只会显示过滤后的元素
        Flux.just(1, 2, 3, 4, 5, 6, 7, 0, 5, 6)
                .doOnNext(e -> System.out.println("1-doOnNext[" + e + "]"))
                .doOnEach(signal -> System.out.println("2-doOnEach[" + signal + "]"))
                .filter(integer -> integer > 5)
                .doOnNext(e -> System.out.println("3-doOnNext[" + e + "]"))
                .subscribe();

    }


    /**
     * 打印doOnXxx的触发时机
     *
     * @author bootystar
     */
    @Test
    void signalTypePrint() throws Exception {
        //空流:  链式API中，下面的操作符，操作的是上面的流。
        //事件感知API：当流发生什么事的时候，触发一个回调,系统调用提前定义好的钩子函数（Hook【钩子函数】）；doOnXxx；
        Flux<Integer> flux = Flux.range(1, 7)
                .delayElements(Duration.ofSeconds(1))
                .doOnComplete(() -> {
                    System.out.println("流正常结束...");
                })
                .doOnCancel(() -> {
                    System.out.println("流已被取消...");
                })
                .doOnError(throwable -> {
                    System.out.println("流出错..." + throwable);
                })
                .doOnNext(element -> {
                    System.out.println("doOnNext...元素到达" + element);
                }); //有一个信号：此时代表完成信号

        // 创建一个自定义的订阅者, 该订阅者可以感知流的各种事件,并在检测到指定数字时抛出异常
        SampleSubscriberOnHook subscriber = new SampleSubscriberOnHook();
        flux.subscribe(subscriber);

        // flux是异步的, 此处需要保持主线程不退出
        System.in.read();
    }


}
