package com.bob.infra.redis.publisher;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.event.RedisRecordFixture.CHAT_TEXT_RECORD;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.infra.redis.record.RedisRecord;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("Redis 이벤트 발행 테스트")
class RedisPublisherTest {

  @InjectMocks
  private RedisPublisher redisPublisher;

  @Mock
  private ChannelTopic channelTopic;

  @Mock
  private RedisTemplate<String, Object> redisTemplate;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(redisPublisher, "topicMap", Map.of("CHAT", channelTopic));
  }

  @Test
  @DisplayName("Redis 메시지 발행 - 성공 테스트")
  void 메시지를_발행할_수_있다() {
    // given
    given(channelTopic.getTopic()).willReturn("notification");
    RedisRecord event = CHAT_TEXT_RECORD;

    // when
    redisPublisher.publish(event);

    // then
    then(redisTemplate).should().convertAndSend(channelTopic.getTopic(), event);
  }

  @DisplayName("Redis 메시지 발행 - 실패 (등록되지 않은 타입)")
  @Test
  void 등록되지_않은_타입이면_메시지를_발행하지_않는다() {
    // given
    String invalidType = "UNKNOWN";
    RedisRecord event = RedisRecord.of(
        MEMBER_ID, invalidType, "id", "유효하지 않은 메시지 타입",null, false,
        OTHER_MEMBER_ID, "TESTER", "Profile"
    );

    // when
    redisPublisher.publish(event);

    // then
    then(redisTemplate).shouldHaveNoInteractions();
  }
}
