package com.example.webflux.clazz;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.SignalType;

/**
 * 继承自BaseSubscriber，可以自定义订阅者
 * @author booty
 */
public class SampleSubscriber<T> extends BaseSubscriber<T> {

    @Override
    public void hookOnSubscribe(Subscription subscription) {
        System.out.println("SampleSubscriber-Subscribed");
        /*
         在操作请求时，您必须小心产生足够的需求推进序列，否则您的Flux可能会“卡住”。
         这就是为什么BaseSubscriber 默认为hookOnSubscribe中的无界请求。
         覆盖此钩子时，您通常应该呼叫request至少一次。
         */
        request(1);

    }

    @Override
    public void hookOnNext(T value) {
        /*
        在反应器中实现背压时，消费者压力传播回源的方式是向上游操作员发送request。
        当前请求的总和有时被称为当前“需求”或“待处理请求”。
        需求上限为Long.MAX_VALUE，代表无界请求（意思是“尽可能快地生产”——基本上禁用反向压力）。
        BaseSubscriber还提供了一个requestUnbounded()方法来切换到无界模式（相当于request(Long.MAX_VALUE)），以及一个cancel()方法
         */
        System.out.println(value);
        request(1);
    }

    //=================以下为BaseSubscriber提供的钩子函数=================

    @Override
    protected void hookOnComplete() {
        System.out.println("数据流结束");
    }

    @Override
    protected void hookOnError(Throwable throwable) {
        System.out.println("数据流异常");
    }

    @Override
    protected void hookOnCancel() {
        System.out.println("数据流被取消");
    }

    @Override
    protected void hookFinally(SignalType type) {
        System.out.println("结束信号：" + type);
    }








}