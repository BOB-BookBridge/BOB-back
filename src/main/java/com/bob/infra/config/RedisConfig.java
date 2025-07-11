package com.bob.infra.config;

import com.bob.infra.redis.subscriber.RedisSubscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@Configuration
public class RedisConfig {

  private final ObjectMapper objectMapper;

  @Value("${spring.data.redis.host}")
  private String redisHost;

  @Value("${spring.data.redis.port}")
  private int redisPort;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
    redisConfiguration.setHostName(redisHost);
    redisConfiguration.setPort(redisPort);
    return new LettuceConnectionFactory(redisConfiguration);
  }

  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(
      RedisConnectionFactory connectionFactory,
      RedisSubscriber redisEventSubscriber,
      Map<String, ChannelTopic> topicMap
  ) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    new HashSet<>(topicMap.values()).forEach(topic -> container.addMessageListener(redisEventSubscriber, topic));
    return container;
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

    GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
    template.setValueSerializer(serializer);
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(serializer);
    return template;
  }

  @Bean
  public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
    StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    return template;
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
