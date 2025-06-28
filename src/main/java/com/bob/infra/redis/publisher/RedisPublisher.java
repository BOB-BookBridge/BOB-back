package com.bob.infra.redis.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisPublisher {

  private final RedisTemplate<String, Object> redisTemplate;
  private final ChannelTopic chatTopic;

  public <T> void publish(T message) {
    redisTemplate.convertAndSend(chatTopic.getTopic(), message);
  }
}
