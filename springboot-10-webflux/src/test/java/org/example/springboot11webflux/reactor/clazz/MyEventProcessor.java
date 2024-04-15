package org.example.springboot11webflux.reactor.clazz;

import java.util.List;

/**
 * 我的事件处理器
 *
 * @author booty
 */
public class MyEventProcessor<T> {
    private MyEventListener<T> listener;

    public void register(MyEventListener<T> listener) {
        this.listener = listener;
    }

    public void processBatch(List<T> chunk) {
        // 生产元素时, 通过监听器发出事件
        listener.onDataChunk(chunk);
    }

    public void processComplete() {
        // 完成生产时, 通过监听器发出事件
        listener.processComplete();
    }


}
