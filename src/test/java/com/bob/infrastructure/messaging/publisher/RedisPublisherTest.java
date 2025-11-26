package com.bob.infrastructure.messaging.publisher;

import static com.bob.support.fixture.event.RedisRecordFixture.CHAT_TEXT_RECORD;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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

import com.bob.infrastructure.messaging.record.RedisRecord;

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
    void 메시지_발행() {
        given(channelTopic.getTopic()).willReturn("notification");
        RedisRecord event = CHAT_TEXT_RECORD;

        redisPublisher.publish(event);

        then(redisTemplate).should().convertAndSend(channelTopic.getTopic(), event);
    }

    @Test
    void 메시지_발행_시_등록되지_않은_타입이면_생략() {
        String invalidType = "UNKNOWN";
        RedisRecord event = RedisRecord.of(
            MEMBER_ID, invalidType, "id", "id", "유효하지 않은 메시지 타입", null, false, false,
            OTHER_MEMBER_ID, "TESTER", "Profile"
        );

        redisPublisher.publish(event);

        then(redisTemplate).shouldHaveNoInteractions();
    }
}
