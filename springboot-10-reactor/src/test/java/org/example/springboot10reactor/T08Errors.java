package org.example.springboot10reactor;

import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 错误处理操作符
 * 响应式编程中的异常处理和try-catch机制类似
 * 在Reactive Streams中，错误是终端事件。一旦发生错误，它就会停止 序列并沿着运算符链传播到最后一步，
 * 1.通过Subscriber的订阅者的errorConsumer处理
 * 2.通过onError()方法处理
 *
 *
 * @author booty
 */
public class T08Errors {


    /**
     * 在订阅时处理异常
     *
     * @author booty
     */
    @Test
    void errorSubscribe() throws Exception{
        Flux<Integer> s = Flux.just(1, 2, 0)
                .map(v -> Math.abs(v-10)) //将V-10取绝对值
                .map(v -> 10/v); // 当v=0时，会除10/0抛出ArithmeticException异常
        s.subscribe(
                value -> System.out.println("RECEIVED " + value), //正常时的逻辑
                error -> System.err.println("CAUGHT " + error) //发生异常时的逻辑
                , () -> System.out.println("DONE") // 最终完成后的逻辑
        );

        /*
        上方写法实际相当于
        try {
            for (int i = 1; i < 11; i++) {
                String v1 = Math.abs(i-10);
                String v2 = 10/i;
                System.out.println("RECEIVED " + v2);
            }
        } catch (Throwable t) {
            System.err.println("CAUGHT " + t);
        }
         */

    }


    /**
     * 处理链中间错误的替代方法，例如错误处理运算符
     * 当产生错误时的替代返回方案, 并终止流
     *
     * @author booty
     */
    @Test
    void onErrorReturn() {
        Flux.just(1, 2, 0, 3, 4)
                .map(i -> "100 / " + i + " = " + (100 / i)) //this triggers an error with 0
                .onErrorReturn("Divided by zero :(")// error handling example
                .subscribe(System.out::println)
        ;
    }

    /**
     * 使用断言来处理错误, 当满足断言时,
     * 返回替代值, 未满足时抛出异常
     *
     *
     * @author booty
     */
    @Test
    void onErrorReturn2() {

        Flux.just(1, 2, 0, 3, 4)
                .map(i -> "100 / " + i + " = " + (100 / i)) //this triggers an error with 0
//                .onErrorReturn(e -> e.getClass().equals(ArithmeticException.class), "recovered10")
                .onErrorReturn(e -> 1==2, "recovered10")
                .subscribe(System.out::println)
        ;
    }

    /**
     * onErrorComplete()
     * 发生异常时, 捕获并忽略异常,并直接结束流(正常结束)
     *
     * @author booty
     */
    @Test
    void onErrorComplete() {
        Flux.just(1, 2, 0, 3, 4)
                .map(i -> "100 / " + i + " = " + (100 / i)) //this triggers an error with 0
                .onErrorComplete()
                .subscribe(System.out::println)
        ;
    }


    /**
     * onErrorResume
     * 方法签名:
     * onErrorResume(Function<? super Throwable, ? extends Publisher<? extends T>> fallback)
     * 仅一个参数fallback, 表示备用策略,
     * 当未发生异常时fallback不会做处理,
     * 当发生异常时, 使用fallback策略返回新的生产者继续生产数据
     *
     * @author booty
     */
    @Test
    void onErrorResume() {
        Flux<String> flux = Flux.just(1, 2, 0, 3, 4)
                .map(i -> "100 / " + i + " = " + (100 / i))
                .onErrorResume(e ->Flux.just("1", "2", "0", "3", "4"))
        ;
        flux.subscribe(System.out::println);
    }


    /**
     * 使用doOnError感知错误, 使用doOnFinally做兜底处理
     * 类似Java中的try catch finally
     *
     * @author booty
     */
    @Test
    void doOnErrorAndDoOnFinally(){
        Flux.just(1, 2, 0, 3, 4)
                .map(i -> "100 / " + i + " = " + (100 / i))
                .doOnError(e -> System.out.println("doOnError: " + e))
                .doFinally(e-> System.out.println("doFinally: " + e))
                .subscribe(System.out::println);
    }


    /**
     * FLux.using()
     * 创建一个资源, 在订阅时使用资源, 在完成时释放资源
     * 类似java语法中的try-with-Resource
     *
     * @author booty
     */
    @Test
    void tryWithResource() throws Exception{
        AtomicBoolean isDisposed = new AtomicBoolean();
        System.out.println("isDisposed: " + isDisposed);
        Disposable disposableInstance = new Disposable() {
            @Override
            public void dispose() {
                isDisposed.set(true);
            }

            @Override
            public String toString() {
                return "DISPOSABLE";
            }
        };
        Flux<String> flux =
                Flux.using(
                        () -> disposableInstance, // 生成资源
                        disposable -> Flux.just(disposable.toString()), // 处理资源
                        Disposable::dispose // 清理释放资源
                );

        // 订阅和执行序列后，isDisposed原子的boolean 变得true。
        flux.subscribe();
        System.out.println("isDisposed: " + isDisposed);
    }


    /**
     * 发生错误时的终止
     * 当发生错误时, 错误终止, 不会继续执行, 生产者后续生产的数据的订阅者将不会收到
     *
     * @author booty
     */
    @Test
    void errorTerminate() throws Exception{
        Flux<String> flux =
                Flux.interval(Duration.ofMillis(250))
                        .map(input -> {
                            if (input < 3) return "tick " + input;
                            throw new RuntimeException("boom");
                        })
                        .onErrorReturn("Uh oh");

        flux.subscribe(System.out::println);
        // 即使多了一秒钟的运行时间，也不会有更多的滴答声从interval进来。这序列确实被错误终止了。

        Thread.sleep(2100);// 确保流在完成前被取消。
    }


    /**
     * 重试机制
     * 当发生错误时, 重新订阅, 重新执行(会完全从头开始执行)
     * Flux.elapsed方法主要用于测量和监控Flux流中元素发射的时间间隔，以及处理可能出现的发射延迟。
     * 这是在构建响应式应用程序时进行性能分析和调试的有用工具
     * 在使用Flux.elapsed时，如果没有发生错误，Flux流将不会自动完成，它将无限期地继续发射元素
     *
     * @author booty
     */
    @Test
    void retry() throws Exception{
        Flux.interval(Duration.ofMillis(250))
                .map(input -> {
                    if (input < 3) return "tick " + input;
                    throw new RuntimeException("boom");
                })
                .retry(1) // 重试一次
                .elapsed()// 测量这些元素发射的实际时间间隔
                .subscribe(System.out::println, System.err::println);
        Thread.sleep(2100);
    }






}
