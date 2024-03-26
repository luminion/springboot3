package org.example.springboot10reactor.base;

import reactor.core.publisher.FluxSink;

import java.util.List;

/**
 * 假设您使用基于侦听器的API。它按块处理数据并且有两个事件：（1）一块数据准备好了，（2）处理完成了 完成（终端事件）
 *
 * @author booty
 */
public class MyEventListenerImpl<T> implements MyEventListener<T> {
    /**
     * 水槽
     * 向flux发送元素
     */
    private FluxSink<T> sink;

    public MyEventListenerImpl(FluxSink<T> sink) {
        this.sink = sink;
    }

    @Override
    public void onDataChunk(List<T> chunk){
        // 监听到生产事件后, 调用sink.next(T)方法, 生产元素
        for (T t : chunk) {
            sink.next(t);// 块中的每个元素都成为Flux中的一个元素。
        }
    };


    @Override
    public void processComplete(){
        // 监听到生产结束事件后, 调用sink.complete()通知生产者结束生产
        sink.complete();
    };
}
