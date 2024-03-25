package org.example.springboot10reactor.base;

import java.util.List;

/**
 * 假设您使用基于侦听器的API。它按块处理数据并且有两个事件：（1）一块数据准备好了，（2）处理完成了 完成（终端事件）
 *
 * @author booty
 */
public interface MyEventListener<T> {
    /**
     * 收到大块数据
     *
     * @param chunk 大块
     * @author booty
     */
    void onDataChunk(List<T> chunk);

    /**
     * 处理完成
     *
     * @author booty
     */
    void processComplete();
}
