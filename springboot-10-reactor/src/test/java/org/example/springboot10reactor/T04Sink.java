package org.example.springboot10reactor;

import org.example.springboot10reactor.base.DoOnSthSubscriber;
import org.example.springboot10reactor.base.MyEventListener;
import org.example.springboot10reactor.base.MyEventProcessor;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 通过可编程方式lux或Mono
 * 以编程方式定义其关联事件（onNext、onError和 onComplete）。
 * 这些方法有一个共通的签名参数, 我们将其称为sink, 用于触发对应的事件
 * 原文:
 * All these methods share the fact that they expose an API to trigger the events that we call a sink.
 * There are actually a few sink variants, which we’ll get to shortly
 *
 * 文档地址:
 * https://projectreactor.io/docs/core/release/reference/
 * @author booty
 */
public class T04Sink {

    /**
     * 通过Flux的generate()创建同步可编程的的Flux
     *
     * Flux的generate()方法有以下方法签名：
     * generate(Supplier<S>, BiFunction<S, SynchronousSink<T>, S>)
     * generate(Supplier<S>, BiFunction<S, SynchronousSink<T>, S>, Consumer<S>)
     * 参数1: 初始数据(元数据)
     * 参数2: 状态更新函数(生产者)
     * 参数3: 状态清理函数(消费者, 处理最后一次生成的元数据)
     *
     *
     * 在参数2 BiFunction<S, SynchronousSink<T>, S>函数中
     * S代表生成器第一次接收到的元数据,
     * SynchronousSink<T> 生成器, 生产出T类型的数据(SynchronousSink中的next(T)方法才是实际给消费者的数据)
     * S代表下次生成器接收到的元数据
     *
     *
     * SynchronousSink表示同步的sink
     * SynchronousSink的生产是同步和逐个的，这意味着next()方法每次回调/回传调用最多一次。
     * 然后您可以额外调用error(Throwable)或complete()来抛出异常或结束流
     *
     * @author booty
     */
    @Test
    void test1() {
        Flux.generate(
                () -> 0, // 初始元数据为0
                (state, sink) -> {
                    sink.next("3 x " + state + " = " + 3*state); // 使用sink来指定实际发送什么给订阅者
                    // sink.next("haha"); //next()方法只能调用一次, 否则会抛出IllegalStateException
                    if (state == 10) sink.complete(); // 若接收到的状态为10，使用sink结束流
                    return state + 1; // 下一次调用中使用的新数据（除非序列在这个调用中终止）
                })
                .subscribe(System.out::println)
                ;
    }

    /**
     * 通过Flux的generate()创建同步可编程的的Flux,并添加状态清理函数
     * generate(Supplier<S>, BiFunction<S, SynchronousSink<T>, S>, Consumer<S>)
     * 参数1: 初始数据
     * 参数2: 状态更新函数(生产者)
     * 参数3: 状态清理函数(消费者, 处理生产者最后一次生成的数据)
     *
     * 此处的状态清理函数,
     * 会在生产者调用sink.complete()后, 并且流结束前触发,
     * 并获取到生产者最后一次生产的元数据
     *
     *
     * @author booty
     */
    @Test
    void test2() {
        // 此写法与上方相同, 只是将元素换为了AtomicLong, 并调用getAndIncrement使其自增
        Flux.generate(
                    AtomicLong::new,
                    (state, sink) -> {
                        long i = state.getAndIncrement();
                        sink.next("3 x " + i + " = " + 3 * i);
                        if (i == 10) sink.complete();
                        return state;
                    },
                    state -> System.out.println("last state: " + state) // 状态清理函数, 获取到的数据是生产者最后一次生产的元数据
                    )
                .subscribe(System.out::println)

        ;

    }


    /**
     * 通过Flux的create()创建异步可编程的的Flux
     *
     * create是Flux的编程创建的更高级形式,
     * 适用于每轮多次调用next(T)方法生产元素，并且可以多线程运行
     * 它公开了一个FluxSink及其next、error和complete方法。
     * 与generate相反，它没有基于状态的变体。
     * 另一方面，它可以在回调/回传中触发多线程事件
     *
     * 注意:
     * create自身并行代码，也不会使其异步,
     * 所以异步并行的逻辑需要自己在外编写
     * 原文:
     * create doesn’t parallelize your code nor does it make it asynchronous,
     * even though it can be used with asynchronous APIs.
     * If you block within the create lambda,
     * you expose yourself to deadlocks and similar side effects.
     * Even with the use of subscribeOn,
     * there’s the caveat that a long-blocking create lambda (such as an infinite loop calling sink.next(t)) can lock the pipeline:
     * the requests would never be performed due to the loop starving the same thread they are supposed to run from.
     * Use the subscribeOn(Scheduler, false) variant: requestOnSeparateThread = false will use the Scheduler thread for the create and still let data flow by performing request in the original thread
     *
     * @author booty
     */
    @Test
    void test3() throws  Exception{

        // 用于生产元素的生产者
        MyEventProcessor<String> myEventProcessor = new MyEventProcessor<>();

        // 技术
        AtomicInteger count = new AtomicInteger();
        Flux.create(
                sink -> {
                    // 给生产者注册事件监听器,监听器监听到对应事件后执行对应操作
                    myEventProcessor.register(
                            new MyEventListener<String>() {
                                // 监听到生产事件后, 调用sink.next(T)方法, 生产元素,
                                public void onDataChunk(List<String> chunk) {
                                    for(String s : chunk) {
                                        sink.next(s);
                                    }
                                }

                                // 监听到生产结束事件后, 调用sink.complete()结束流
                                public void processComplete() {
                                    sink.complete();
                                }
                            });
                })
                // 订阅,打印元素的获取序号, 生产元素的线程和序号
                .subscribe(e->{
                    System.out.printf("%s---%s%n",count.getAndIncrement(),e);
                })
        ;

        // 多线程生产元素, 模拟生产者
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            String thread = String.format("thread[%s]",i);
            pool.submit(()->{
                ArrayList<String> strings = new ArrayList<>(10);
                for (int j = 0; j < 10; j++) {
                    strings.add(String.format("%s-element:[%s]",thread,j));
                }
                myEventProcessor.processBatch(strings);
                // 当任意一个生产者生产完数后, 就认为处理完成(因为是多线程环境, 所以实际停止时消费的元素的个数不定)
                myEventProcessor.processComplete();
            });
        }

        // 主线程休眠10秒
        Thread.sleep(1000*10);
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
    void test9() {
        // doOnNext和doOnEach的区别
        // doOnNext：每个数据（流的数据）到达的时候触发,只能接收到数据
        // doOnEach：每个元素（流的数据和信号）到达的时候触发, 同时可以接收到数据和信号
        // 同时, doOnXxx要感知某个流的事件，写在这个流的后面，新流的前面,
        // 例如下面的filter之后, 便产生了一个新流, 第二个doOnNext就不会感知之前流的动作,只会显示过滤后的元素
        Flux.just(1, 2, 3, 4, 5, 6, 7, 0, 5, 6)
                .doOnNext(integer -> System.out.println("1-doOnNext[" + integer+"]"))
                .doOnEach(integerSignal -> System.out.println("2-doOnEach[" + integerSignal+"]"))
                .filter(integer -> integer > 5)
                .doOnNext(integer -> System.out.println("3-doOnNext[" + integer+"]"))
                .subscribe();

    }

    @Test
    void test11() throws Exception {
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

        // 创建一个自定义的订阅者, 该订阅者可以感知流的各种事件,并在检测到指定数字时抛出异常
        DoOnSthSubscriber doOnSthSubscriber = new DoOnSthSubscriber();
        flux.subscribe(doOnSthSubscriber);

        // flux是异步的, 此处需要保持主线程不退出
        System.in.read();
    }
}
