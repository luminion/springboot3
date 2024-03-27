package org.example.springboot10reactor.base;

import java.util.List;

/**
 * 假设您使用基于侦听器的API。它按块处理数据并且有两个事件：（1）一块数据准备好了，（2）处理完成了 完成（终端事件）
 *
 * @author booty
 */
public interface MyEventListener<T> {
    /**
     * 触发批量生产事件
     *
     * @param elements 需要生产的元素集合
     * @author booty
     */
    void onDataChunk(List<T> chunk);

    /**
     * 触发完成生产事件
     *
     * @author booty
     */
    void processComplete();
}
