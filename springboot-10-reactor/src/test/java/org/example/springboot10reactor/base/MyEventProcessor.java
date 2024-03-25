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
     * 批量处理元素
     *
     * @param event 事件
     * @author booty
     */
    public void processBatch(List<T> event) {
        listener.onDataChunk(event);
    }

    /**
     * 完成处理
     *
     * @author booty
     */
    public void processComplete() {
        listener.processComplete();
    }


}
