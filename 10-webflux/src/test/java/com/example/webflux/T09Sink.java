package com.example.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 在Reactor中，Sink是一种类，它可以安全地独立触发信号，从而创建一个类似发布者的结构，能够处理多个订阅者（除了unicast()类型的情况）。
 *
 * 在3.5.0版本之前，也存在一系列Processor实现，但已被逐步淘汰。
 *
 * Sinks的主要类别包括：
 * many().multicast()：这种Sink只会向其订阅者传输新的推送数据，尊重他们的背压（“新推送”的含义是在订阅者订阅之后的数据）。
 * many().unicast()：与上述相同，但具有一个特点：在第一个订阅者注册之前推送的数据会被缓冲。
 * many().replay()：这种Sink会向新订阅者重播一定数量的历史推送数据，然后继续实时推送新数据。
 * one()：这种Sink会向其订阅者播放单个元素。
 * empty()：这种Sink只会向其订阅者播放终止信号（错误或完成），但仍可视为Mono<T>（注意这里的泛型类型<T>）
 *
 *
 * @author booty
 */
public class T09Sink {


    /**
     * 使用Sinks.One和Sinks.Many安全地在多线程环境下生产数据 Reactor-Core提供的默认Sinks实现确保了多线程使用的检测，
     * 并且不会导致下游订阅者视角下的规范违规或未定义行为。
     * 当使用tryEmit* API时，平行调用会快速失败。当使用emit* API时，
     * 提供的EmissionFailureHandler可能允许在竞争情况下（例如繁忙循环）重试，否则Sink将以错误状态终止。
     * 相比于Processor.onNext，这是一次改进，因为Processor.onNext必须外部进行同步，否则可能导致下游订阅者视角下的未定义行为。
     * 处理器（Processors）是一种特殊类型的发布者，同时也是订阅者。它们最初被设计为一种可能的中间步骤表示，
     * 可以在不同的Reactive Streams实现之间共享。然而，在Reactor中，这样的中间步骤通常由作为发布者的操作符来表现。
     * 初次接触处理器时，常见错误是想要直接从Subscriber接口调用暴露的onNext、onComplete和onError方法。
     * 此类手动调用应谨慎进行，特别是在与Reactive Streams规范相关的外部调用同步方面。
     * 实际上，除非遇到需要传入订阅者而非公开发布者的基于Reactive Streams的API，处理器的实际用途可能相当有限。
     * Sink通常是更好的选择。
     *
     *
     * @author booty
     */
    @Test
    void createSink() throws Exception{
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();
        // 同样地，Sinks.Empty和Sinks.One类型可以通过asMono()方法视作Mono

        ExecutorService pool = Executors.newFixedThreadPool(4);
        // 线程1
        pool.submit(() -> replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST));
        // 线程2
        pool.submit(() -> replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST));
        // 线程3，与线程2并发 // 如果未能成功，则会尝试重试2秒并最终抛出EmissionException
        // 在使用busyLooping时要注意，返回的EmitFailureHandler实例不可重用，即每调用一次emitNext应对应一个busyLooping调用。
        // 此外，建议设置超时时间大于100毫秒，因为更小的值在实践中意义不大
        pool.submit(() -> replaySink.emitNext(3, Sinks.EmitFailureHandler.busyLooping(Duration.ofSeconds(2))));
        Sinks.EmitResult result = replaySink.tryEmitNext(4);
        System.out.println(result);
        Flux<Integer> fluxView = replaySink.asFlux();
        Integer blockLast = fluxView
                .takeWhile(i -> i < 10)
                .log()
                .blockLast(); // 阻塞等待完成信号
        // 发送完成信号
        replaySink.emitComplete(Sinks.EmitFailureHandler.FAIL_FAST);

        System.out.println(blockLast);

    }

    /*
    基本的单播Sink通过Sinks.many().unicast().onBackpressureBuffer()创建。
    而在Sinks.many().unicast()中还有一些额外的静态工厂方法，提供了更细粒度的调整功能。

    例如，默认情况下它是无界的：如果在订阅者尚未请求数据时就通过它推送任意数量的数据，它会缓存所有数据。
    可以通过在Sinks.many().unicast().onBackpressureBuffer(Queue)工厂方法中提供自定义队列实现来更改此设置。
    如果该队列是有界的，当缓冲区满并且来自下游的请求不足时，sink可能会拒绝推送值。
        Sinks.many().multicast().onBackpressureBuffer(args?)：
        一个多播Sink可以向多个订阅者发送信号，同时尊重每个订阅者的背压。订阅者只接收他们在订阅之后通过Sink推送的信号。

    基本的多播Sink通过Sinks.many().multicast().onBackpressureBuffer()创建。
    通过在Sinks.many().multicast()中的multicast静态工厂方法中使用autoCancel参数，
    可以调整当所有订阅者取消订阅（即全部已退订）时的行为，如是否清空内部缓冲区并停止接受新订阅者。

    此外还介绍了多种多播Sink变体，
    包括Sinks.many().multicast().directAllOrNothing()
    和Sinks.many().multicast().directBestEffort()，
    以及具有历史记录限制、基于时间的重播窗口等多种配置的Sinks.many().replay()。

        Sinks.unsafe().many()：
        高级用户和操作符构建者可能考虑使用Sinks.unsafe().many()，
        它提供了相同的Sinks.Many工厂方法，但不包含额外的生产者线程安全性。
        这样每个Sink的开销较小，因为线程安全的Sink必须检测多线程访问。

    库开发者不应公开不安全的Sink，但在受控调用环境中可以内部使用，
    确保对导致onNext、onComplete和onError信号的方法调用进行外部同步，遵循Reactive Streams规范。

        Sinks.one()：
        该方法直接构建一个简单的Sinks.One<T>实例。
        这种Sinks类型可通过其asMono()视图方法视为Mono，并具有一些略有不同的emit方法，以更好地传达Mono类似的语义。
        Sinks.empty()：
        该方法直接构建一个简单的Sinks.Empty<T>实例，与Sinks.One<T>相似，但它不具备emitValue方法。
        因此，它只能生成一个完整或失败的Mono。
        尽管无法触发onNext，但由于方便组合和包含在要求特定类型的运算符链中，该Sink仍然带有泛型<T>。
     */
}
