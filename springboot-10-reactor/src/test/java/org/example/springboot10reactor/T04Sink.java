package org.example.springboot10reactor;

import org.example.springboot10reactor.base.DoOnSthSubscriber;
import org.example.springboot10reactor.base.MyEventProcessor;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 通过可编程方式lux或Mono
 * 以编程方式定义其关联事件（onNext、onError和 onComplete）。
 * 这些方法有一个共通的签名参数, 我们将其称为sink, 用于触发对应的事件
 * 原文:
 * All these methods share the fact that they expose an API to trigger the events that we call a sink.
 * There are actually a few sink variants, which we’ll get to shortly
 * <p>
 * 文档地址:
 * https://projectreactor.io/docs/core/release/reference/
 *
 * @author booty
 */
public class T04Sink {

    /**
     * 通过Flux的generate()创建同步可编程的的Flux
     * <p>
     * 方法签名：
     * generate(Supplier<S>, BiFunction<S, SynchronousSink<T>, S>)
     * generate(Supplier<S>, BiFunction<S, SynchronousSink<T>, S>, Consumer<S>)
     * 参数1: 初始数据(元数据)
     * 参数2: 状态更新函数(生产者)
     * 参数3: 状态清理函数(消费者, 处理最后一次生成的元数据)
     * <p>
     * <p>
     * 在参数2 BiFunction<S, SynchronousSink<T>, S>函数中
     * S代表生成器第一次接收到的元数据,
     * SynchronousSink<T> 生成器, 生产出T类型的数据(SynchronousSink中的next(T)方法才是实际给消费者的数据)
     * S代表下次生成器接收到的元数据
     * <p>
     * <p>
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
                            sink.next("3 x " + state + " = " + 3 * state); // 使用sink来指定实际发送什么给订阅者
                            // sink.next("haha"); //next()方法只能调用一次, 否则会抛出IllegalStateException
                            if (state == 10) sink.complete(); // 若接收到的状态为10，使用sink结束流
                            return state + 1; // 下一次调用中使用的新数据（除非序列在这个调用中终止）
                        })
                .subscribe(System.out::println)
        ;
    }

    /**
     * 通过Flux的generate()创建同步可编程的的Flux,并添加状态清理函数
     * <p>
     * 方法签名
     * generate(Supplier<S>, BiFunction<S, SynchronousSink<T>, S>, Consumer<S>)
     * 参数1: 初始数据
     * 参数2: 状态更新函数(生产者)
     * 参数3: 状态清理函数(消费者, 处理生产者最后一次生成的数据)
     * <p>
     * 此处的状态清理函数,
     * 会在生产者调用sink.complete()后, 并且流结束前触发,
     * 并获取到生产者最后一次生产的元数据
     * 在状态包含数据库连接或其他资源的情况下 需要在流程结束时处理，清理函数可以执行关闭连接等操作
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
     * <p>
     * 方法签名:
     * create(Consumer<? super FluxSink<T>>)
     * 参数1: 表示生产者,生产者通过FluxSink来指定实际发送什么给订阅者
     * <p>
     * <p>
     * <p>
     * create是Flux的编程创建的更高级形式,
     * 适用于每轮多次调用next(T)方法生产元素，并且可以多线程运行
     * 它公开了一个FluxSink及其next、error和complete方法。
     * 与generate相反，它没有基于状态的变体。
     * 另一方面，它可以在回调/回传中触发多线程事件
     * <p>
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
    void test3() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        Flux.create(sink -> {
            for (int i = 0; i < 20; i++) {
                AtomicInteger num = new AtomicInteger(i);
                pool.submit(() -> {
                    sink.next(Thread.currentThread().getName() + ":" + num.get());
                });
            }
        }).subscribe(System.out::println);

        Thread.sleep(1000 * 10);
    }

    /**
     * 通过Flux的create()创建异步可编程的的Flux, 并使用网桥到异步API
     * <p>
     * 方法签名:
     * create(Consumer<? super FluxSink<T>>)
     * 参数1: 表示生产者,生产者通过FluxSink来指定实际发送什么给订阅者
     * <p>
     * <p>
     * 1.创建一个MyEventProcessor作为生产者
     * 2.通过Flux的create()创建Flux,在创建时将sink与生产者中间进行绑定(网桥)
     * 3.通过多线程并发调用MyEventProcessor的processBatch(T)方法创建元素
     * 4.MyEventProcessor的processBatch方法被调用的时, 会调用内部绑定的sink的next(T)方法生产元素
     * 至此,可以通过外部控制MyEventProcessor异步生产元素
     * 同时,任意一个线程生产完元素后, 就调用complete结束流
     * 由于是多线程, 所以其结束的时机是不确定的, 若生产者生产的速度足够快, 则会在发出信号->收到信号期间继续生产元素
     *
     * @author booty
     */
    @Test
    void test4() throws Exception {

        // 用于生产元素的生产者, 由于需要多线程,所以在外部定义
        MyEventProcessor<String> myEventProcessor = new MyEventProcessor<>();
        // 计数
        AtomicInteger count = new AtomicInteger();
        // 创建flux
        Disposable subscribe = Flux
                .create(myEventProcessor::register) // 将Flux中的sink注册绑定到实际生产者中
                .subscribe(e -> System.out.printf("%s---%s%n", count.getAndIncrement(), e));// 订阅,打印元素的获取序号, 生产元素的线程和序号

        // 多线程生产元素, 模拟生产者
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            String thread = String.format("thread[%s]", i);
            pool.submit(() -> {
                try {
                    // 休眠一秒,确保其他线程创建完毕,之后再多线程并发
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (int j = 0; j < 10; j++) {
                    String format = String.format("%s-element:[%s]", Thread.currentThread().getName(), j);
                    myEventProcessor.process(format);
                }
                // 当任意一个生产者生产完数后, 就认为处理完成(因为是多线程环境, 所以实际停止时消费的元素的个数不定)
                myEventProcessor.processComplete();
            });
        }

        // 主线程休眠10秒
        Thread.sleep(1000 * 10);
    }

    /**
     * 通过Flux的create()创建异步可编程的的Flux, 使用网桥到异步API,通过指示OverflowStrategy来改进如何表现反向压力
     * <p>
     * 方法签名:
     * create(Consumer<? super FluxSink<T>>, OverflowStrategy)
     * 参数1: 表示生产者,生产者通过FluxSink来指定实际发送什么给订阅者
     * 参数2: 背压的管理策略
     * <p>
     * <p>
     * FluxSink.OverflowStrategy有以下策略
     * BUFFER：
     * 缓冲区模式，会将生产者发出的所有元素存放在内部缓冲区中，直到消费者能够消费它们为止。
     * 这意味着如果生产速率持续高于消费速率，缓冲区可能会无限制地增长，可能导致内存溢出问题。
     * DROP：
     * 抛弃模式，当内部缓冲区已满并且有新的元素到来时，此策略会丢弃新来的元素，如果下游没有准备好接收传入信号，则丢弃传入。
     * ERROR：
     * 错误模式，当缓冲区满时，会抛出一个BufferOverflowException异常，终止序列的处理。
     * LATEST：
     * 最新值模式，只保留最新推送的元素，每当有新的元素到达时，它会替换掉缓冲区中的旧元素。
     * IGNORE：
     * 忽略模式，当内部缓冲区满时，此策略则会直接忽略新来的元素，既不会添加到缓冲区中，也不会替换掉原有的元素，而且同样允许生产者继续发送数据。
     * 与 drop 不同的是，它并不涉及任何内部数据的移动或替换操作，而是完全忽视那些无法处理的元素，没有进一步的通知或错误抛出。
     * <p>
     * 此外Mono也有一个create生成器MonoSink的创建 该创建器不允许多次调用。它将在第一次之后丢弃所有信号
     *
     * @author booty
     */
    @Test
    void test5() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final int[] a = {0};
        Flux.push(t-> {
                    for (int i=0;i<10;i++){
                        t.next(a[0]++);
                        try {
                            TimeUnit.MICROSECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println("generate thread:"+Thread.currentThread().getName());
                    t.complete();
                }, FluxSink.OverflowStrategy.DROP)
                .publishOn(Schedulers.newSingle("publish-thread-"),1)
                .subscribeOn(Schedulers.newSingle("subscribe-thread-"))
                .subscribe(new Subscriber<Object>() {
                    private Subscription subscription = null;

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        this.subscription = subscription;
                        subscription.request(1);
                    }

                    @Override
                    public void onNext(Object o) {
                        System.out.println(Thread.currentThread().getName()+":消费数据:"+o);
                        try {
                            TimeUnit.MICROSECONDS.sleep(30);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        this.subscription.request(1);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("出现错误");
                        throwable.printStackTrace();
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Complete");
                        countDownLatch.countDown();
                    }
                });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 试验5
     *
     * @author booty
     */
//    @Test
//    void test5() throws Exception{
//
//    }





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
                .doOnNext(integer -> System.out.println("1-doOnNext[" + integer + "]"))
                .doOnEach(integerSignal -> System.out.println("2-doOnEach[" + integerSignal + "]"))
                .filter(integer -> integer > 5)
                .doOnNext(integer -> System.out.println("3-doOnNext[" + integer + "]"))
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
