package com.bob.infrastructure.messaging.adapter;

import static com.bob.support.fixture.event.RedisRecordFixture.TRADE_RECORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bob.infrastructure.messaging.publisher.RedisPublisher;
import com.bob.infrastructure.messaging.record.RedisRecord;

@DisplayName("알림 메시지 adapter 테스트")
@ExtendWith(MockitoExtension.class)
class NotiRedisAdapterTest {

    @Mock
    private RedisPublisher redisPublisher;

    @InjectMocks
    private NotificationMessageAdapter notiRedisAdapter;

    @Test
    void 발행자_호출() {
        RedisRecord record = TRADE_RECORD;

        notiRedisAdapter.publish(
            record.receiverId(), record.type(), record.refId(), record.childId(), record.body(), record.fileNames(),
            record.isSystem(), record.normalize(), record.sender().id(), record.sender().nickname(),
            record.sender().profile()
        );

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
