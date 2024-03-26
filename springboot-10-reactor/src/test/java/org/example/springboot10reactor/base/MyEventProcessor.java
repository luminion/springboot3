package org.example.springboot10reactor.base;

import java.util.List;

/**
 * 我的事件处理器
 *
 * @author booty
 */
public class MyEventProcessor<T> {
    private MyEventListener<T> listener;
    
    /**
     * 注册监听器
     *
     * @param listener 监听器
     * @author booty
     */
    public void register(MyEventListener<T> listener) {
        this.listener = listener;
    }

    /**
     * 生产元素
     *
     * @param event 需要生产的元素集合
     * @author booty
     */
    public void processBatch(List<T> event) {
        listener.onDataChunk(event);
    }

    /**
     * 完成生产
     *
     * @author booty
     */
    public void processComplete() {
        listener.processComplete();
    }


}
