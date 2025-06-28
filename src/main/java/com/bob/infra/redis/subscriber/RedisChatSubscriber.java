package com.bob.infra.redis.subscriber;

import com.bob.domain.chat.service.dto.event.RedisChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisChatSubscriber implements MessageListener {

  private final RedisMessageListenerContainer redisContainer;
  private final ChannelTopic chatTopic;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @PostConstruct
  public void init() {
    redisContainer.addMessageListener(this, chatTopic);
  }

  @Override
  public void onMessage(Message message, byte[] pattern) {
    try {
      String body = new String(message.getBody());
      RedisChatMessage chatMessage = objectMapper.readValue(body, RedisChatMessage.class);
      log.debug("Received Redis ChatMessage: {}", chatMessage);

      // TODO: 채팅 기능 구현 시 key = chatRoom sse send 호출
    } catch (Exception e) {
      log.error("Failed to deserialize Redis ChatMessage", e);
    }
  }
}
