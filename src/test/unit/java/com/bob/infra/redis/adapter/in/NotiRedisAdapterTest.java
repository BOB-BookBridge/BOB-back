package com.bob.infra.redis.adapter.in;

import static com.bob.support.fixture.event.RedisRecordFixture.TRADE_RECORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.bob.infra.redis.publisher.RedisPublisher;
import com.bob.infra.redis.record.RedisRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("NotiRedisAdapter 테스트")
@ExtendWith(MockitoExtension.class)
class NotiRedisAdapterTest {

  @Mock
  private RedisPublisher redisPublisher;

  @InjectMocks
  private NotiRedisAdapter notiRedisAdapter;

  @Test
  @DisplayName("redis publisher 호출 테스트")
  void publish_정상_호출_및_값_검증() {
    // given
    RedisRecord record = TRADE_RECORD;

    // when
    notiRedisAdapter.publish(
        record.receiverId(), record.type(), record.refId(), record.body(),
        record.normalize(), record.sender().id(), record.sender().nickname(), record.sender().profile()
    );

    // then
    ArgumentCaptor<RedisRecord> captor = ArgumentCaptor.forClass(RedisRecord.class);
    verify(redisPublisher).publish(captor.capture());
    RedisRecord published = captor.getValue();

    assertThat(published.type()).isEqualTo(record.type());
    assertThat(published.refId()).isEqualTo(record.refId());
    assertThat(published.body()).isEqualTo(record.body());
    assertThat(published.receiverId()).isEqualTo(record.receiverId());
    assertThat(published.normalize()).isEqualTo(record.normalize());
    assertThat(published.sender().id()).isEqualTo(record.sender().id());
    assertThat(published.sender().nickname()).isEqualTo(record.sender().nickname());
    assertThat(published.sender().profile()).isEqualTo(record.sender().profile());
  }
}
