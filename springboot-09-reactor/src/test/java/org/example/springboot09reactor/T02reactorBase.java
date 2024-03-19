package org.example.springboot09reactor;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class T02reactorBase {

    /*
    spring中reactor中的流有2种,
    Flux: 0-N个元素

    Mono: 0-1个元素
     */

    @Test
    void test1() throws InterruptedException {
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
        Thread.sleep(6000);

    }


    @Test
    void test2() throws InterruptedException {
        // 创建一个Flux, 包含1~7的数字
        Flux<Integer> range = Flux.range(1, 7);
        range.filter(i -> i > 3) //挑出>3的元素
                .delayElements(Duration.ofSeconds(1)) //获取元素后延迟1秒再获取下一个元素
                .log()  //日志打印, 可以重复添加,根据不同位置打印不同内容, 目前打印4-7;添加到filter之前, 则会打印1-7, 添加到map后,则会打印转化为字符串后的内容
                .map(i -> "Str-" + i) // 在数字的前方添加字母将其转为字符串
                .subscribe(System.out::println); //订阅, 打印出每个元素
        // 一个流的所有操作都在subscribe时发生, 若没有subscribe, 则不会有任何操作

        // flux是异步的, 此处需要保持主线程不退出
        Thread.sleep(6000);
    }


    /*
    响应式编程核心：
    SignalType：(触发信号)
          SUBSCRIBE： 被订阅
          REQUEST：  请求了N个元素
          CANCEL： 流被取消
          ON_SUBSCRIBE：在订阅时候
          ON_NEXT： 在元素到达
          ON_ERROR： 在流错误
          ON_COMPLETE：在流正常完成时
          AFTER_TERMINATE：中断以后
          CURRENT_CONTEXT：当前上下文
          ON_CONTEXT：感知上下文
     doOnXxx (API触发时机)
          1、doOnNext：每个数据（流的数据）到达的时候触发
          2、doOnEach：每个元素（流的数据和信号）到达的时候触发
          3、doOnRequest： 消费者请求流元素的时候
          4、doOnError：流发生错误
          5、doOnSubscribe: 流被订阅的时候
          6、doOnTerminate： 发送取消/异常信号中断了流
          7、doOnCancel： 流被取消
          8、doOnDiscard：流中元素被忽略的时候
     */
    @Test
    void test3() {
        // 关键：doOnNext：表示流中某个元素到达以后触发我一个回调
        // doOnXxx要感知某个流的事件，写在这个流的后面，新流的前面
        Flux.just(1, 2, 3, 4, 5, 6, 7, 0, 5, 6)
                .doOnNext(integer -> System.out.println("元素到达：" + integer)) //元素到达得到时候触发
                .doOnEach(integerSignal -> { //each封装的详细
                    System.out.println("doOnEach.." + integerSignal);
                })//1,2,3,4,5,6,7,0
                .map(integer -> 10 / integer) //10,5,3,
                .doOnError(throwable -> {
                    System.out.println("数据库已经保存了异常：" + throwable.getMessage());
                })
                .map(integer -> 100 / integer)
                .doOnNext(integer -> System.out.println("元素到达：" + integer))
                .subscribe(System.out::println);

    }

    @Test
    void test4() throws Exception {
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
                .doOnNext(integer -> {
                    System.out.println("doOnNext..." + integer);
                }); //有一个信号：此时代表完成信号

        flux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("订阅者和发布者绑定好了：" + subscription);
                request(1); //背压
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println("元素到达：" + value);
                if (value < 5) {
                    request(1);
                    if (value == 3) {
                        int i = 10 / 0;
                    }
                } else {
                    cancel();//取消订阅
                }
                ; //继续要元素
            }

            @Override
            protected void hookOnComplete() {
                System.out.println("数据流结束");
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                System.out.println("数据流异常");
            }

            @Override
            protected void hookOnCancel() {
                System.out.println("数据流被取消");
            }

            @Override
            protected void hookFinally(SignalType type) {
                System.out.println("结束信号：" + type);
                // 正常、异常
//                try {
//                    //业务
//                }catch (Exception e){
//
//                }finally {
//                    //结束
//                }
            }
        });

        Thread.sleep(2000);


//        Flux<Integer> range = Flux.range(1, 7);


        System.in.read();
    }


}
