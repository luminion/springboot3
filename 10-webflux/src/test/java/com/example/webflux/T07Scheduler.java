package com.example.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 响应式编程的Schedulers（调度器）是用于控制任务执行的策略和线程池。
 * 在响应式编程中，调度器负责将任务分配到合适的线程上执行，以便充分利用系统资源并提高程序的性能。
 * 响应式编程中的Schedulers提供了多种方法来满足不同的执行需求。以下是一些常用的方法及其特点：
 * immediate()：
 * 此方法返回的调度器会在当前线程上立即执行任务。
 * single()：
 * 创建一个单线程的调度器，所有调用者都会重用同一个线程，直到调度器的状态被设置为disposed。
 * elastic()：
 * 提供无界的弹性线程池，适合将阻塞操作放在单独的线程中执行，使其变为异步。
 * 随着有界弹性线程池的出现, 该方法不再是首选, 因为它有隐藏背压的趋势，请使用boundedElastic()代替。
 * boundedElastic()：
 * 提供有界弹性线程池，适合将阻塞操作放在单独的线程中执行，使其变为异步。
 * 给阻塞进程一个单独线程,这样它就不会占用其他资源,适合于IO操作
 * parallel()：
 * 创建并行调度器，使用固定大小的线程池来并行处理任务。
 * io()：
 * 专门用于IO密集型任务的调度器，通常与网络请求或文件操作相关联。
 * computation()：
 * 计算调度器，适用于计算密集型任务，线程池大小通常等于处理器数量。
 * newThread()：
 * 为每个任务创建新线程的调度器，适用于简单的并发任务。
 * trampoline()：
 * 在当前线程上执行任务，适用于简单的任务，可以避免线程切换的开销。
 * 在实际应用中，可以通过`publishOn()`和`subscribeOn()`方法来改变执行上下文。
 * `publishOn()`用于指定下游操作的调度器，
 * 而`subscribeOn()`则用于指定上游操作的调度器。
 * 这些方法的使用可以帮助开发者更好地控制任务的执行策略，从而提高响应式程序的性能和效率。
 * @author booty
 */
public class T07Scheduler {

    /**
     * 获得Flux或Mono并不一定意味着它在专用 Thread。
     * 相反，大多数运算符继续在当前线程中工作执行前一个运算符。
     * 除非指定，否则最顶层的运算符（源） 将在当前Thread上运行
     *
     * @author booty
     */
    @Test
    void threadTest() throws Exception{
        // 在主线程创建Mono<String>
        final Mono<String> mono = Mono.just("hello ");
        // 新建线程订阅Mono
        Thread t = new Thread(
                () -> mono
                .map(msg -> msg + "thread ")
                .subscribe(v -> System.out.println(v + Thread.currentThread().getName()))
                // 实际的操作都在订阅时的线程中执行
        );
        t.start();
        t.join();
    }


    /**
     * 使用Schedulers让响应式程序在特定的线程上执行
     * 可以使用Schedulers.newXXX()创建各种调度程序类型的newXXX方法
     * 例如，Schedulers.newParallel(name1)创建一个新的并行 调度程序名称为name1
     *
     * @author booty
     */
    @Test
    void scheduler() throws Exception {
        /*
        调用 Flux.interval(Duration.ofMillis(300))工厂方法产生一个Flux<Long>，每300毫秒滴答一次。
        默认情况下，这由Schedulers.parallel()启用
        此处通过调度器指定单线程调度器，因此，每次间隔300毫秒，都会在单独的线程上执行。
         */
        Flux.interval(Duration.ofMillis(300),  Schedulers.newSingle("test"))
                .subscribe(v -> System.out.println(v + " " + Thread.currentThread().getName()));
        TimeUnit.SECONDS.sleep(10);
    }


    /**
     * publishOn()
     * 用于指定在哪个调度器（Scheduler）上执行后续的操作。
     * 它允许你在不同的线程或上下文中执行操作，以便更好地控制异步任务的执行
     *
     *
     * @author booty
     */
    @Test
    void publicOn() throws Exception{
        // 创建一个新的Scheduler，由四个Thread实例支持
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);

        final Flux<String> flux = Flux
                .range(1, 2)
                .map(i -> {
                    System.out.println("beforePublishOn===>" + Thread.currentThread().getName()+"===>value:"+i);
                   return  10 + i;
                })
                .publishOn(s)
                .map(i -> {
                    System.out.println("afterPublishOn===>" + Thread.currentThread().getName()+"===>value:"+i);
                    return "value " + i;
                });

        new Thread(() -> flux.subscribe(e-> System.out.println("OnScribe===>" + Thread.currentThread().getName()+"===>value:"+e)),"subscribe-thread").start();
        ;

        /*
        输出结果:
        beforePublishOn===>subscribe-thread===>value:1
        beforePublishOn===>subscribe-thread===>value:2
        afterPublishOn===>parallel-scheduler-1===>value:11
        OnScribe===>parallel-scheduler-1===>value:value 11
        afterPublishOn===>parallel-scheduler-1===>value:12
        OnScribe===>parallel-scheduler-1===>value:value 12
        结论:
        采用publishOn()方法，可以指定在哪个调度器（Scheduler）上执行后续的操作。
        但是对于publishOn()之前的方法, 会在订阅者线程中处理
         */
        TimeUnit.SECONDS.sleep(10);
    }


    /**
     * subscribeOn()
     * 在Spring Reactor的Flux中，subscribeOn和publishOn都是用于指定异步操作执行的调度器，但它们的作用范围和使用场景有所不同。
     *
     * 作用范围：
     * subscribeOn：当一个Flux被订阅时，subscribeOn会指定整个流（包括所有的操作符）在哪个调度器上执行。它影响从源头开始的整个执行过程，无论它在链中的何处被调用。
     * publishOn：publishOn仅影响其之后的operator执行的线程池。它的作用范围取决于它在链中的位置，只对其下方的操作符有效。
     * 使用场景：
     * subscribeOn：通常用于想要确保整个流在特定的调度器上执行的情况，比如IO密集型任务或者需要特殊线程管理的场景。
     * publishOn：当你想让流的一部分操作在特定的调度器上执行，而不是整个流时，可以使用publishOn。这在你需要对特定操作进行优化，比如将计算密集型任务移动到具有更多核心的调度器上时非常有用。
     *
     * 同时使用subscribeOn和publishOn时，subscribeOn会首先起作用，确保整个流在指定的调度器上运行。
     * 然后，publishOn会将其之后的操作符转移到另一个指定的调度器上执行。
     * 这种组合使用可以实现复杂的线程切换和任务执行策略，以适应不同的性能需求和资源管理
     *
     * @author booty
     */
    @Test
    void subscribeOn() throws Exception{
        // 创建一个新的Scheduler，由四个Thread实例支持
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);

        final Flux<String> flux = Flux
                .range(1, 2)
                .map(i -> {
                    System.out.println("beforeSubscribeOn===>" + Thread.currentThread().getName()+"===>value:"+i);
                    return  10 + i;
                })
                .subscribeOn(s)
                .map(i -> {
                    System.out.println("afterSubscribeOn===>" + Thread.currentThread().getName()+"===>value:"+i);
                    return "value " + i;
                });

        new Thread(() -> flux.subscribe(e-> System.out.println("OnScribe===>" + Thread.currentThread().getName()+"===>value:"+e)),"subscribe-thread").start();;

        /*
        输出结果:
        beforeSubscribeOn===>parallel-scheduler-1===>value:1
        afterSubscribeOn===>parallel-scheduler-1===>value:11
        OnScribe===>parallel-scheduler-1===>value:value 11
        beforeSubscribeOn===>parallel-scheduler-1===>value:2
        afterSubscribeOn===>parallel-scheduler-1===>value:12
        OnScribe===>parallel-scheduler-1===>value:value 12
        结论:
        采用subscribeOn()方法，可以将操作切换到指定线程上运行,届时所有操作都会在指定线程运行
         */
        TimeUnit.SECONDS.sleep(10);
    }


//    @Test
//    void threadTest() throws Exception{
//        Flux.just(10)
//                .map(this::doSomethingDangerous)
//                .onErrorReturn(e -> e.getMessage().equals("boom10"), "recovered10")
//    }





}
