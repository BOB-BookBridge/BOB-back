package com.bob.infra.redis.subscriber;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bob.domain.chat.service.dto.event.RedisChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisChatSubscriber 테스트")
class RedisChatSubscriberTest {

  @InjectMocks
  private RedisChatSubscriber redisChatSubscriber;

  @Mock
  private RedisMessageListenerContainer redisContainer;

  @Mock
  private ChannelTopic chatTopic;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @DisplayName("Redis 메시지 수신 - 역직렬화 성공")
  void redis_메시지_수신_성공() throws Exception {
    // given
    RedisChatMessage chatMessage = new RedisChatMessage();
    String json = objectMapper.writeValueAsString(chatMessage);

    Message redisMessage = mock(Message.class);
    when(redisMessage.getBody()).thenReturn(json.getBytes(StandardCharsets.UTF_8));

    // when & then
    assertThatCode(() -> redisChatSubscriber.onMessage(redisMessage, null)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Redis 메시지 수신 - 역직렬화 실패")
  void redis_메시지_수신_실패() {
    // given
    String invalidJson = "잘못된 json 문자열";

    Message redisMessage = mock(Message.class);
    when(redisMessage.getBody()).thenReturn(invalidJson.getBytes(StandardCharsets.UTF_8));

    // when & then
    assertThatCode(() -> redisChatSubscriber.onMessage(redisMessage, null)).doesNotThrowAnyException();
  }
}