package org.example.springboot10reactor;

import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

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
                    value -> System.out.println("RECEIVED " + value),
                    error -> System.err.println("CAUGHT " + error)
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

}
