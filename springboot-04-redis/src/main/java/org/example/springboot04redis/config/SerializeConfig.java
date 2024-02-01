package org.example.springboot04redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * redis的默认序列化策略为JdkSerializationRedisSerializer,
 * 若需要序列化的类没有实现serializable接口，则会报错
 * 这个序列化策略的缺点是：储存在redis中的数据是乱码的,无法通过可视化工具查看
 * 此处使用json序列化策略
 * @author booty
 */
@Configuration
public class SerializeConfig {

    /**
     * 注入redisTemplate, 指定将对象序列化为json字符串储存
     * 注意:
     * 若在此处指定了泛型, 则在使用时必须指定相同的泛型, 否则会报错
     * 若未指定泛型, 则在使用时可以指定任意泛型
     *
     * @param redisConnectionFactory redis连接工厂(默认为lettuce,可通过配置文件更改)
     * @return {@link RedisTemplate }<{@link Object }, {@link Object }>
     * @author booty
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        //把对象转为json字符串的序列化工具
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

}
