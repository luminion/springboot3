package com.example.kafka.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主题配置
 * 用于向kafka中添加topic并指定类型过期策略等
 * 如果您在应用程序上下文中定义了KafkaAdmin bean，它可以自动向代理添加主题。
 * 为此，可以将每个主题的NewTopic 通过@Bean添加到应用程序上下文。
 * 版本2.3引入了一个新的类TopicBuilder，使创建这样的bean更方便
 *
 * @author booty
 */
@Configuration
public class ConfigTopic {

    /**
     * 配置kafka管理员(用于创建主题)
     * 默认使用springboot时, 默认已经创建了一个KafkaAdmin bean, 无需再次创建
     * 仅当发现没有自动创建时, 才需要注入此bean
     *
     * 从2.7开始, 可以使用KafkaAdmin在运行时创建主题
     * 1.createOrModifyTopics()方法用于创建或修改主题
     * 2.describeTopics()方法用于获取主题的详细信息
     * @return {@link KafkaAdmin }
     * @author booty
     */
//    @Bean
    public KafkaAdmin admin(@Value("${spring.kafka.bootstrap-servers}")String bootstrapServers) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name("thing1") //主题名称
                .partitions(1) // 分区数(不能大于broker(kafka实例)数)
                .replicas(1) // 副本数(不能大于broker(kafka实例)数)
                .compact() // 设置清除策略为压缩
                .build();
    }

//    @Bean
    public NewTopic topic2() {
        return TopicBuilder.name("thing2") // 主题名称
                .partitions(10) // 分区数(不能大于broker(kafka实例)数)
                .replicas(3)    // 副本数(不能大于broker(kafka实例)数)
                .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "zstd") // 设置压缩类型
                .build();
    }

    /**
     * topic3
     *
     * @return {@link NewTopic }
     * @author booty
     */
//    @Bean
    public NewTopic topic3() {
        return TopicBuilder.name("thing3") // 主题名称
                .assignReplicas(0, List.of(0, 1)) // 分区0的副本分配到broker0和broker1
                .assignReplicas(1, List.of(1, 2)) // 分区1的副本分配到broker1和broker2
                .assignReplicas(2, List.of(2, 0)) // 分区2的副本分配到broker2和broker0
                .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "zstd")    // 设置压缩类型
                .build();
    }
}
