package org.example.springboot11webflux.reactor;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 背压和请求重塑
 * Backpressure 背压
 * Reshape Requests 请求重塑
 * Prefetch 预取
 *
 * 文档地址:
 * https://projectreactor.io/docs/core/release/reference/
 *
 * @author booty
 */
public class T05Backpressure {

    /**
     * 背压
     * 背压是指由消费者来控制生产者生产数据的速度
     * 例:
     * 当一个发布者向订阅者发送数据时，如果订阅者没有准备好接收这些数据，那么消费者可以让发布者停止发送数据，
     * 当订阅者准备好接收数据时,可以通知发送者, 让发送者发送指定数量的数据
     *
     * @author booty
     */
    @Test
    void receiveAndCancel(){
        // 接收到一个元素后取消订阅
        Flux.range(1, 10)
                .doOnRequest(r -> System.out.println("request of " + r))
                .subscribe(new BaseSubscriber<Integer>() {
                    private AtomicInteger counter = new AtomicInteger(1);
                    @Override
                    public void hookOnSubscribe(Subscription subscription) {
                        // 获取5个数据
                        request(5);
                    }
                    @Override
                    public void hookOnNext(Integer integer) {
                        System.out.println(integer);
                        int andIncrement = counter.getAndIncrement();
                        // 获取到3次数据后取消订阅
                        if (andIncrement == 3) {
                            System.out.println("Cancelling after having received " + integer);
                            cancel();
                        }

                    }
                });
    }



    /**
     * 请求重塑之buffer
     * 在订阅级别表达的需求可以由上游链中的每个运算符重塑。
     * 例如是buffer(N)运算符：如果它收到一个request(2)，
     * 它被解释为对两个完整缓冲区的需求。 因此，由于缓冲区需要N元素才能被视为满，因此buffer运算符将请求重塑为2 x N
     * 换言之
     * buffer操作可以将流中的元素按照指定的数量进行缓存(由生产者进行缓存,一次生产多少个元素)
     * 当消费者获取元素时, 将会获取到一个List集合, 集合中包含了指定数量的元素(若不足则全部)
     *
     * @author booty
     */
    @Test
    void bufferSubscribe() throws Exception {
        Flux.range(1, 10)
                .buffer(3) // 生产者每3个元素缓存一次
                .subscribe(System.out::println);
    }


    /**
     * 请求重塑之flatMap
     * flatMap操作符可以将每个元素转换为一个流(可以添加元素)，然后将这些流合并为一个单独的流。
     * flatMap和buffer的区别,
     * buffer是将元素缓存,未改变原元素,
     * flatMap是将元素转换为流, 可以添加额外元素, 也可以不添加
     *
     * @author booty
     */
    @Test
    void flatMapSubscribe() throws Exception {
        Flux.range(1, 5)
                .flatMap(e -> Flux.just(e + 10,e)) // 重塑流,将原有元素自身和+10后的元素添加到新流中
                .subscribe(System.out::println);
    }

    /**
     * 请求预取
     * 预取是一种调整对这些内部序列发出的初始请求的方法。 如果未指定，默认取32个元素。
     * limitRate(N)拆分请求数量，以便它们以较小的批次向传播。
     *
     * 预取的补充最优化：
     * 一旦消费者消费了预取指定值N中的75%，消费者就会继续请求75%的数据(向上取整)。
     * 这是一种启发式最优化，以便这些运算符主动预测即将到来的请求
     *
     * 例如，
     * limitRate(10), 生产者生产100元素, 消费者每次只会请求10个元素
     * 和buffer的不同点:buffer是指定生产者每次生产的元素个数, limitRate是指定消费者每次请求的元素个数
     *
     *
     * @author booty
     */
    @Test
    void limitRate() throws Exception {
        Flux.range(1, 100)
                .doOnRequest(e-> System.out.println("request = " + e)) // 感知预取的请求
                .limitRate(10) // 限制每次处理的元素数量
                .subscribe(System.out::println);
    }


    /**
     * limitRequest(N)将目标端请求限制为最大总需求。
     * 它将请求加起来最多N。如果单个request没有使总需求溢出超过N，则该特定请求将完全向上游传播。
     * 源发出该数量后，limitRequest认为序列完成，向目标端发送onComplete信号，并取消
     *
     * @author booty
     */
    @Test
    void limitRequest() throws Exception {
        Flux.range(1, 100)
                .doOnRequest(e -> System.out.println("request = " + e)) // 感知预取的请求
                .limitRequest(10) // 限制总共只取10个
                .subscribe(System.out::println);
    }

}
