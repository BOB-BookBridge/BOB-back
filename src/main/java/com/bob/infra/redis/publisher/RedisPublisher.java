package com.bob.infra.redis.publisher;

import com.bob.infra.redis.record.RedisRecord;
import java.util.Map;
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
  private final Map<String, ChannelTopic> topicMap;

  public void publish(RedisRecord message) {
    ChannelTopic topic = topicMap.get(message.type());
    if (topic == null) {
      log.warn("unknown message type : {}", message.type());
      return;
    }
    log.debug("publish redis record : topic={}, type={}, refId={}", topic.getTopic(), message.type(), message.refId());
    redisTemplate.convertAndSend(topic.getTopic(), message);
  }
}
