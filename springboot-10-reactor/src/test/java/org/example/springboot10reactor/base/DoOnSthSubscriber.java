package org.example.springboot10reactor.base;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.SignalType;

public class DoOnSthSubscriber extends BaseSubscriber<Integer> {
    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        System.out.println("订阅者和发布者绑定好了：" + subscription);
        request(1); //背压
    }

    @Override
    protected void hookOnNext(Integer value) {
        System.out.println("元素到达：" + value);
        if (value < 5) {
            request(1);
            if (value == 3) {
                // 当元素为3时，抛出异常, 触发doOnError事件感知
                int i = 10 / 0;
            }
        } else {
            cancel();//取消订阅
        }
        ; //继续要元素
    }

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
        // 正常、异常
//                try {
//                    //业务
//                }catch (Exception e){
//
//                }finally {
//                    //结束
//                }
    }


}
