package org.example.springboot07kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * kafka生产者配置
 * springboot默认使用127.0.0.1:9092作为kafka服务器
 * 简单配置时, 该类可以不用编写，通过配置文件配置即可
 *
 * 编码过程
 * 1.配置生产者工厂
 * 2.配置生产者配置
 * 3.配置kafka模板
 *
 *
 * @author booty
 */
@Configuration
//@EnableKafka // 高版本springboot中不需要此注解(3.0初的低版本需通过此注解开启kafka注解驱动功能)
public class ConfigFactoryTemplate {

    /**
     * 生产者工厂
     * 若在配置文件中指定了,则不需要在此处编码
     * 此处测试多kafkaTemplate实例, 所以使用@Value注入配置文件中的kafka.bootstrap-servers属性
     *
     * @param bootstrapServers 此处读取默认配置文件中的kafka.bootstrap-servers属性
     * @return {@link ProducerFactory }<{@link String }, {@link Object }>
     * @author booty
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }


    /**
     * kafka模板
     *
     * @return {@link KafkaTemplate }<{@link Integer }, {@link String }>
     * @author booty
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> pf) {
        return new KafkaTemplate<String, Object>(pf);
    }


    /*
    从版本2.5开始，可以覆盖工厂的ProducerConfig属性，以从同一工厂创建具有不同生产者配置的模板
    注意: 若使用泛型, 会导致类型不匹配, 无法注入, 此处可以不使用泛型注入工厂
    如下:
     */


    /**
     * 由于kafka模板的value类型是byte[], 所以需要重新配置生产者工厂
     * 若未配置指定泛型的工厂到容器中, 此处会报错
     *
     * @param pf pf
     * @return {@link KafkaTemplate }<{@link String }, {@link byte[] }>
     * @author booty
     */
    //    @Bean
    public KafkaTemplate<String, byte[]> bytesTemplateUnable(ProducerFactory<String, byte[]> pf) {
        return new KafkaTemplate<>(pf,
                Collections.singletonMap(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class));
    }

    /**
     * 相比上面方法, 该方法不使用泛型, 所以可以直接复用上面注入到容器的的ProducerFactory<String, Object>工厂
     * 并通过配置覆盖的方式, 指定生产者配置的value序列化器为byte[]
     *
     * @param pf pf
     * @return {@link KafkaTemplate }<{@link String }, {@link byte[] }>
     * @author booty
     */
    @Bean
    public KafkaTemplate<String, byte[]> bytesTemplate(ProducerFactory pf) {
        return new KafkaTemplate<>(pf,
                Collections.singletonMap(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class));
    }


}
