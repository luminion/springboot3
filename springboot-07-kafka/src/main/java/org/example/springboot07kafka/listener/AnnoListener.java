package org.example.springboot07kafka.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;

/**
 * 注解方式的监听器
 * 注解@KafkaListener注释用于将bean方法指定为侦听器容器的侦听器。
 * bean包装在MessagingMessageListenerAdapter中，
 * 该适配器配置有各种功能，例如用于转换数据的转换器，如果需要，以匹配方法参数。
 *
 * @author booty
 */
//@Component
public class AnnoListener {

    /**
     * 监听kafka的order主题
     * 使配置后只会监听到发给kafka的新消息，以前的拿不到
     *
     * @param record 记录
     * @author booty
     */
    @KafkaListener(topics = "order", //监听的主题
            groupId = "order-service"// 消费者组
    )
    public void listen(ConsumerRecord<String,Object> record){
        System.out.println("收到消息："+record); //可以监听到发给kafka的新消息，以前的拿不到
    }

    /**
     * 监听指定主题的所有消息(包括历史消息)
     *
     * @param record 记录
     * @author booty
     */
    @KafkaListener(
            groupId = "order-service-2", // 消费者组
            topicPartitions = {
            @TopicPartition(topic = "order", // 监听的主题
                    partitionOffsets = {
                    @PartitionOffset(partition = "0",initialOffset = "0") // 指定获取消息的位置, 从0开始
            })
    })
    public void listenAll(ConsumerRecord<String,Object> record){
        System.out.println("收到partition-0消息："+record);
    }

}
