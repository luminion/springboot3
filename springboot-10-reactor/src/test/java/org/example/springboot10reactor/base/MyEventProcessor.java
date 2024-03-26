package org.example.springboot10reactor.base;

import reactor.core.publisher.FluxSink;

/**
 * 我的事件处理器
 *
 * @author booty
 */
public class MyEventProcessor<T> implements MyEventListener<T> {
    private FluxSink<T> sink;

    public void register(FluxSink<T> sink) {
        this.sink = sink;
    }

    @Override
    public void process(T t) {
        // 生产元素时, 通过sink向Flux中添加元素
        sink.next(t);
    }

    @Override
    public void processComplete() {
        // 使用用sink.complete()通知生产者结束生产
        sink.complete();
    }


}
