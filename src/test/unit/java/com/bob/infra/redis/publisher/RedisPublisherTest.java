package com.bob.infra.redis.publisher;

import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisPublisher 테스트")
class RedisPublisherTest {

  @InjectMocks
  private RedisPublisher redisPublisher;

  @Mock
  private ChannelTopic channelTopic;

  @Mock
  private RedisTemplate<String, Object> redisTemplate;

  @Test
  @DisplayName("Redis 메시지 발행 - 성공")
  void 메시지를_발행할_수_있다() {
    // given
    String message = "hello";

    // when
    redisPublisher.publish(message);

    // then
    then(redisTemplate).should().convertAndSend(channelTopic.getTopic(), message);
  }
}
