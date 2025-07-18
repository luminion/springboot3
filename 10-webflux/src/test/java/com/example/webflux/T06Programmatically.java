package com.example.webflux;

import com.example.webflux.clazz.MyEventListener;
import com.example.webflux.clazz.MyEventListenerSingleThread;
import com.example.webflux.clazz.MyEventProcessor;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 通过可编程方式Flux或Mono
 * 以编程方式定义其关联事件（onNext、onError和 onComplete）。
 * 这些方法有一个共通的签名参数, 我们将其称为sink, 用于触发对应的事件
 * 原文:
 * All these methods share the fact that they expose an API to trigger the events that we call a sink.
 * There are actually a few sink variants, which we’ll get to shortly
 *
 * 文档地址:
 * https://projectreactor.io/docs/core/release/reference/
 *
 * @author booty
 */
public class T06Programmatically {

    /**
     * Flux.generate()
     * 创建同步的Flux
     *
     * 方法签名：
     * Flux<T> generate(Supplier<S>, BiFunction<S, SynchronousSink<T>, S>)
     * Flux<T> generate(Supplier<S>, BiFunction<S, SynchronousSink<T>, S>, Consumer<S>)
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
    void fluxGenerate() {
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
     * Flux.generate()
     * 创建同步的Flux,并添加状态清理函数
     *
     * 方法签名
     * Flux<T> generate(Supplier<S>, BiFunction<S, SynchronousSink<T>, S>, Consumer<S>)
     * 参数1: 初始数据
     * 参数2: 状态更新函数(生产者)
     * 参数3: 状态清理函数(消费者, 处理生产者最后一次生成的数据)
     *
     * 此处的状态清理函数,
     * 会在生产者调用sink.complete()后, 并且流结束前触发,
     * 并获取到生产者最后一次生产的元数据
     * 在状态包含数据库连接或其他资源的情况下 需要在流程结束时处理，清理函数可以执行关闭连接等操作
     *
     * @author booty
     */
    @Test
    void fluxGenerateWithCleanup() {
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
     * Flux.create()
     * 创建异步多线程的Flux
     *
     * 方法签名:
     * Flux<T> create(Consumer<? super FluxSink<T>>)
     * 参数1: 表示生产者,生产者通过FluxSink来指定实际发送什么给订阅者
     *
     *
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
    void fluxCreate() throws Exception {
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
     * Flux.create()
     * 创建异步多线程的Flux,并使用网桥API达成多线程异步生产元素
     *
     * 方法签名:
     * Flux<T> create(Consumer<? super FluxSink<T>>)
     * Flux<T> create(Consumer<? super FluxSink<T>>, OverflowStrategy)
     * 参数1: 表示生产者,生产者通过FluxSink来指定实际发送什么给订阅者
     * 参数2: 背压的管理策略(详情见下方法)
     *
     *
     * 1.创建一个MyEventProcessor作为生产者
     * 2.通过Flux的create()创建Flux,在创建时创建一个MyEventListener绑定sink,将其注册到MyEventProcessor,
     * 3.通过多线程并发调用MyEventProcessor的processBatch(T)方法创建元素
     * 4.MyEventProcessor的processBatch方法被调用的时, 会调用内部绑定的MyEventListener的onDataChunk方法
     * 5.MyEventListener监听到onDataChunk方法触发后,会调用sink的对应方法将生产元素或结束生产
     * 至此,可以通过外部控制MyEventProcessor异步生产元素
     * 同时,任意一个线程生产完元素后, 就调用complete结束流
     * 由于是多线程, 所以其结束的时机是不确定的, 若生产者生产的速度足够快, 则会在发出信号->收到信号期间继续生产元素
     *
     *
     * 此外Mono也有一个create生成器MonoSink的创建,但该创建器不允许多次调用。它将在第一次之后丢弃所有信号
     * @author booty
     */
    @Test
    void fluxCreateWithBridgeAPI() throws Exception {

        // 用于生产元素的生产者, 由于需要多线程,所以在外部定义
        MyEventProcessor<String> myEventProcessor = new MyEventProcessor<>();
        // 计数
        AtomicInteger count = new AtomicInteger();
        // 创建flux
        Disposable subscribe = Flux
                .create(sink->{
                    // 将Flux中的sink注册绑定到实际生产者中(网桥)
                    myEventProcessor.register(new MyEventListener<>() {
                        public void onDataChunk(List<String> chunk) {
                            for (String s : chunk) {
                                sink.next(chunk);
                            }

                        }
                        public void processComplete() {
                            sink.complete();
                        }
                    });
                })
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
                List<String> elements = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    String format = String.format("%s-element:[%s]", thread, j);
                    elements.add(format);
                }
                myEventProcessor.processBatch(elements);
                // 当任意一个生产者生产完数后, 就认为处理完成(因为是多线程环境, 所以实际停止时消费的元素的个数不定)
                myEventProcessor.processComplete();
            });
        }

        // 主线程休眠10秒
        Thread.sleep(1000 * 10);
    }

    /**
     * OverflowStrategy背压策略
     *
     *
     * FluxSink.OverflowStrategy有以下策略
     * BUFFER：
     * 缓冲区模式，当下游处理速度跟不上上游数据的产生速度时，它会尝试缓冲所有的数据项。
     * 这可能会导致无限制的缓冲，极端情况下可能引发OutOfMemoryError异常。
     * LATEST：
     * 最新值模式，这种策略下，下游只会接收上游最新的数据项。
     * 如果上游发送的数据过多，那么除了最新的数据，其他都会被丢弃。
     * DROP：
     * 抛弃模式，当下游无法跟上上游数据的发送速度时，上游会丢弃一些数据项。
     * 这意味着如果消费者处理不过来，一些数据将不会保存起来供后续处理。
     * ERROR：
     * 错误模式，当下游无法跟上上游数据的发送速度时，上游会发出一个IllegalStateException异常信号。
     * 这是一种比较激进的策略，因为它会导致流程的停止。
     * IGNORE：
     * 忽略模式，此策略下，上游会忽略下游的背压信号，继续以自己的速度发送数据。
     * 如果下游处理队列已满，这可能会导致IllegalStateException异常。
     *
     * 背压策略的ERROR和IGNORE的主要区别在于它们处理下游无法跟上上游数据发送速度时的机制不同。具体如下:
     * IGNORE策略：上游将忽略下游的背压信号，继续以自己的速度发送数据。
     *            这意味着无论下游是否能够处理这些数据，上游都会持续推送，可能导致下游处理队列过载甚至引发异常。
     * ERROR策略： 当下游无法跟上上游数据的发送速度时，上游会发出一个异常信号，通常是IllegalStateException。
     *            这种策略相当于告诉系统，如果无法按照预期处理数据，那么就会停止整个流程。
     *
     * @author booty
     */
    @Test
    void overflowStrategyTest() throws Exception {
        /*
        模拟快速的生产者和慢速的消费者, 不同策略下的表现
        BUFFER: 依次消费所有数据
        LATEST: 消费0,1,9
        DROP:   消费0,1
        IGNORE: 消费0,1,2,3,4,5,6,7,8后, 抛出reactor.core.Exceptions$OverflowException: Queue is full: Reactive Streams source doesn't respect backpressure
        ERROR:  消费0,1后抛出reactor.core.Exceptions$OverflowException: The receiver is overrun by more signals than expected (bounded queue...)
         */
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final int[] a = {0};
        Flux.push(t-> {
                    for (int i=0;i<10;i++){
                        t.next(a[0]++);
                        try {
                            TimeUnit.MICROSECONDS.sleep(15);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println("generate thread:"+Thread.currentThread().getName());
                    t.complete();
                }, FluxSink.OverflowStrategy.ERROR)
                .publishOn(Schedulers.newSingle("publish-thread-"),2)
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
                            // 假设消费数据较慢, 模拟慢速的消费者
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
     * Flux.push()
     * 方法签名:
     * Flux<T> push(Consumer<? super FluxSink<T>>)
     *
     * 创建异步单线程的Flux
     * push的速度和开销都介于generate和create之间,
     * generate:    单线程同步
     * push:        单线程异步
     * create:      多线程异步
     *
     * 该方法类似于Flux.generate()方法，但是它允许你处理异常。
     * 适用于处理来自单个生产者的事件。类似于create，因为它也可以是异步的，并且可以使用create支持的任何溢出策略来管理反压。
     * 但是，同一时间只有一个生产者线程能调用next(),complete()和error()方法。
     *
     *
     *
     * @author booty
     */
    @Test
    void fluxPush() throws Exception {
        MyEventProcessor<String> myEventProcessor = new MyEventProcessor<>();
        Flux<String> bridge = Flux.push(sink -> {
            myEventProcessor.register(
                    new MyEventListenerSingleThread<String>() {

                        public void onDataChunk(List<String> chunk) {
                            for (String s : chunk) {
                                sink.next(s);
                            }
                        }

                        public void processComplete() {
                            sink.complete();
                        }

                        public void processError(Throwable e) {
                            sink.error(e);
                        }
                    });
        });
    }


    /**
     * A hybrid push/pull model
     * 混合推/拉模型
     *
     * 响应式编程中的A hybrid push/pull model是一种结合了推送（push）和拉取（pull）模式的数据处理机制。
     * 大多数创建的Flux和Mono都遵循了这个模式
     * 首先，在传统的拉取模式中，数据消费者主动从生产者那里请求数据，即消费者知道何时需要数据并主动去"拉"数据。
     * 这种方式通常适用于消费者能够预测数据需求的场景。
     * 相对地，在推送模式中，数据生产者在数据可用时主动将数据发送给消费者，消费者被动接收数据，这适用于生产者无法预测数据需求时间的情况。
     * 其次，在响应式编程的上下文中，这种混合模式允许系统在不同场景下选择最合适的数据流动方式。
     *
     *
     * Flux.push()
     * Flux.create()
     * Mono.push()
     *
     * void onRequest(long n)
     * void onCancel()
     * void onDispose()
     *
     */
    @Test
    void hybridPushPullModel() throws Exception {
        Flux.create(sink -> {
                    sink.onRequest(n -> System.out.println("FlowSink Request:"+n))
                            .onCancel(() -> System.out.println("FlowSink Cancel"))
                            .onDispose(() -> System.out.println("FlowSink Dispose"));
                })
                .subscribe()

        ;
    }

    /**
     * Flux.handle()
     * 方法签名:
     * Flux<R> handle(BiConsumer<T, SynchronousSink<R>>)
     * 参数1: 消费者, 接收T类型元素,将其转化为R类型元素
     *
     * 该方法接近于stream流中的flatmap方法, 可以将流做转化后继续处理,并可以进行一对多或多对一的转化
     * @author booty
     */
    @Test
    void test8() throws Exception {
        Flux.just(-1, 30, 13, 9, 20)
                .handle((i, sink) -> {
                    // 接收int类型的i, 过滤在范围内的元素,将其转化为对应的字母并发送
                    if (i >= 1 && i <= 26) {
                        int letterIndexAscii = 'A' + i - 1;
                        String letter = ""+ (char) letterIndexAscii;
                        sink.next(letter);
                    }
                })
                .subscribe(System.out::println);

    }






}
