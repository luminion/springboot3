package com.example.webflux.clazz;

/**
 * 事件侦听器单线程
 *
 * @author booty
 */
public interface MyEventListenerSingleThread<T> extends MyEventListener<T> {

    /**
     * 触发错误事件
     *
     * @param e e
     * @author booty
     */
    void processError(Throwable e);

}
