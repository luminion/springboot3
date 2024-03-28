package org.example.springboot10reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

/**
 * @author booty
 */
public class T07Thread {

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
     * 在实际应用中，可以通过`publishOn()`和`subscribeOn()`方法来改变执行上下文。`publishOn()`用于指定下游操作的调度器，而`subscribeOn()`则用于指定上游操作的调度器。这些方法的使用可以帮助开发者更好地控制任务的执行策略，从而提高响应式程序的性能和效率。
     *
     *
     * @author booty
     */
    @Test
     void schedulerTest() throws Exception {

    }


}
