package org.example.springboot11webflux.reactor.clazz;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * 自定义处理器, 同时是订阅者和发布者，接口的定义也是继承了两者，
 * 作为订阅者接收数据，然后进行处理，处理完后再发布出去
 * 继承JDK的SubmissionPublisher, 以便使用其默认实现
 *
 * @author booty
 */
public class MyProcessor extends SubmissionPublisher<String> implements Flow.Processor<String,String>{
    /**
     * 前缀
     */
    protected final String prefix;
    public MyProcessor(String prefix) {
        this.prefix = prefix;
    }


    private Flow.Subscription subscription; //保存绑定关系
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        // 该方法会在绑定订阅关系时调用
        // 保存订阅关系, 需要用它来给发布者响应
        this.subscription = subscription;
        // 绑定完成后, 找自己的上一个处理器(即发布者)要一个数据
        subscription.request(1);
    }

    @Override
    public void onNext(String item) {
        // 该方法会在接收到数据时调用
        // 处理数据, 并发布
        System.out.println(prefix+"拿到数据：["+item+"]");
        // 进行处理, 加上前缀
        item = prefix + "-" + item;
        // 发布(让下一个处理器处理)
        submit(item);
        // 处理完后, 继续请求一个数据
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println(prefix+"出现异常: "+throwable.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println(prefix+"处理完成!");
    }


}
