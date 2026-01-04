package com.bob.infrastructure.messaging.config;

import java.util.HashSet;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.bob.infrastructure.messaging.subscriber.RedisSubscriber;

@Configuration
public class RedisMessagingConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
        RedisConnectionFactory connectionFactory,
        RedisSubscriber redisEventSubscriber
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        new HashSet<>(topicMap().values()).forEach(topic -> container.addMessageListener(redisEventSubscriber, topic));

        return container;
    }

    @Bean
    public Map<String, ChannelTopic> topicMap() {
        return Map.of(
            "CHAT", new ChannelTopic("notification"),
            "TRADE", new ChannelTopic("notification"),
            "CHAT_MESSAGE", new ChannelTopic("chatroom")
        );
    }
}
