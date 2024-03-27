package org.example.springboot10reactor;

import org.example.springboot10reactor.base.MyProcessor;
import org.junit.jupiter.api.Test;

import java.util.concurrent.SubmissionPublisher;

/**
 * jdk9中的Flow类, 定义了发布者，订阅者，处理器等接口
 * 响应式变成的标准规则即基于这些接口
 *
 * @author booty
 */
public class T01Jdk9Flow {
    /*
    java.util.concurrent.Flow是jdk9引入的一个类，用于支持reactive stream
    其中有以下几个重要的接口：

    Subscription:
        即订阅关系,Subscription用于连接 Publisher 和 Subscriber。
        订阅者(Subscriber)只有在请求时才会收到项目，并可以通过Subscription取消订阅。
        Subscription主要有两个方法：
        request：订阅者调用此方法请求数据；
        cancel：订阅者调用这个方法来取消订阅，解除订阅者与发布者之间的关系。
    Publisher
        即发布者,Publisher 将数据流发布给注册的 Subscriber。
        它通常使用Executor 异步发布项目给订阅者。需要确保每个订阅的Subscriber方法严格按顺序调用。
        Java 9 提供了 SubmissionPublisher的默认实现
        它有一个方法：
        subscribe：方法用于订阅者订阅发布者
    Subscriber
        即订阅者,Subscriber订阅Publisher的数据流，并接受回调。
        如果 Subscriber 没有发出请求，就不会受到数据。
        Java 9 提供了对于给定订阅（Subscription），调用 Subscriber的方法是严格按顺序的并在项目到达时执行操作。
        ConsumerSubscriber
        它有三个方法：
        onSubscribe：订阅者调用此方法订阅发布者；
        onNext：发布者调用此方法发送项目；
        onError：发布者调用此方法发送错误；
        onComplete：发布者调用此方法发送完成信号。
    Processor
        即处理器, 同时是订阅者和发布者，接口的定义也是继承了两者，
        作为订阅者接收数据，然后进行处理，处理完后再发布出去
        Processor 位于 Publisher 和 Subscriber 之间，用于做数据转换。
        可以有多个 Processor 同时使用，组成一个处理链，链中最后一个处理器的处理结果发送给 Subscriber


    背压 BackPressure
    Subscriber 向 Publisher 请求消息，并通过提供的回调方法被激活调用。
    如果 Publisher 的处理能力比 Subscriber 强得多，需要有一种机制使得 Subscriber 可以通知 Publisher 降低生产速度。
    Publisher 实现这种功能的机制被称为背压
    以下场景设计即为背压机制：:
        1.Subscriber在调用subscription.request(N)时, 可以根据自己的处理能力来决定要多少数据, 这就是背压的概念
        2.如果Subscriber没有调用Subscriber.request(N)方法, 则Publisher不会发送数据给Subscriber
     */



    @Test
    void flowTest(){
        // 1、定义一个发布者； 发布数据 ,SubmissionPublisher为jdk提供的默认实现；
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

        MyProcessor processor1 = new MyProcessor("p1");
        MyProcessor processor2 = new MyProcessor("p2");
        MyProcessor processor3 = new MyProcessor("p3");
        // 创建检测到数字5就抛出异常的处理器
        MyProcessor processor4 = new MyProcessor("p4"){
            @Override
            public void onNext(String item) {
                System.out.println(prefix+"拿到数据：["+item+"]");
                if (item.contains("5")){
                    // 抛出异常后, 会终止该流, 不会再有数据传递
                    throw new RuntimeException("检测到数字5,抛出异常");
                }
                super.onNext(item);
            }
        };
        // 绑定处理关系
        publisher.subscribe(processor1); //此时处理器相当于订阅者
        processor1.subscribe(processor2); //此时处理器相当于发布者
        processor2.subscribe(processor3);
        processor3.subscribe(processor4);
        //绑定操作；就是发布者，记住了所有订阅者都有谁，有数据后，给所有订阅者把数据推送过去。

        for (int i = 0; i < 7; i++) {
            //发布5条数据
            publisher.submit("*"+i+"*");
        }

        //关闭发布者(可以使用try-with-resource自动关闭)
        publisher.close();

    }


}
