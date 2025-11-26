package com.bob.infrastructure.messaging.subscriber;

import static com.bob.support.fixture.event.RedisRecordFixture.CHAT_IMAGE_RECORD;
import static com.bob.support.fixture.event.RedisRecordFixture.CHAT_MIX_RECORD;
import static com.bob.support.fixture.event.RedisRecordFixture.CHAT_TEXT_RECORD;
import static com.bob.support.fixture.event.RedisRecordFixture.TRADE_RECORD;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.argThat;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.redis.connection.Message;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.bob.global.event.sse.manager.EmitterManager;
import com.bob.global.event.sse.manager.type.EmitterType;
import com.bob.global.event.sse.repository.chat.ChatEmitterKey;
import com.bob.global.event.sse.repository.notification.NotiEmitterKey;
import com.bob.infrastructure.messaging.record.RedisRecord;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisSubscriber 테스트")
class RedisSubscriberTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @InjectMocks
    private RedisSubscriber redisSubscriber;
    @Mock
    private EmitterManager emitterManager;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(redisSubscriber, "objectMapper", objectMapper);
    }

    @Test
    void 채팅_메시지_처리() throws Exception {
        RedisRecord record = CHAT_TEXT_RECORD;
        ChatEmitterKey chatKey = ChatEmitterKey.of(Long.valueOf(record.refId()), record.receiverId());
        given(emitterManager.isExistConnection(EmitterType.CHAT, chatKey)).willReturn(true);

        Message message = mock(Message.class);
        given(message.getBody()).willReturn(objectMapper.writeValueAsBytes(record));

        assertThatCode(() -> redisSubscriber.onMessage(message, null)).doesNotThrowAnyException();
        then(emitterManager).should().sendEvent(eq(EmitterType.CHAT), eq(chatKey), any(), any());
    }

    @Test
    void 채팅_메시지_처리_시_채팅방_Emitter_미존재_시_알림_전송() throws Exception {
        RedisRecord record = CHAT_TEXT_RECORD;
        ChatEmitterKey chatKey = ChatEmitterKey.of(Long.valueOf(record.refId()), record.receiverId());
        NotiEmitterKey notiKey = NotiEmitterKey.of(record.receiverId());
        given(emitterManager.isExistConnection(EmitterType.CHAT, chatKey)).willReturn(false);
        given(emitterManager.isExistConnection(EmitterType.NOTIFICATION, notiKey)).willReturn(true);
        Message message = mock(Message.class);
        given(message.getBody()).willReturn(objectMapper.writeValueAsBytes(record));

        assertThatCode(() -> redisSubscriber.onMessage(message, null)).doesNotThrowAnyException();

        then(emitterManager).should().sendEvent(eq(EmitterType.NOTIFICATION), eq(notiKey), any(), any());
    }

    @Test
    void 메시지_이미지_처리() throws Exception {
        RedisRecord record = CHAT_IMAGE_RECORD;
        ChatEmitterKey chatKey = ChatEmitterKey.of(Long.valueOf(record.refId()), record.receiverId());
        given(emitterManager.isExistConnection(EmitterType.CHAT, chatKey)).willReturn(true);
        Message message = mock(Message.class);
        given(message.getBody()).willReturn(objectMapper.writeValueAsBytes(record));

        assertThatCode(() -> redisSubscriber.onMessage(message, null)).doesNotThrowAnyException();

        then(emitterManager).should().sendEvent(eq(EmitterType.CHAT), eq(chatKey), any(), argThat(event ->
            event.toString().contains("IMAGE")
        ));
    }

    @Test
    void 메시지_혼합형_처리() throws Exception {
        RedisRecord record = CHAT_MIX_RECORD;
        ChatEmitterKey chatKey = ChatEmitterKey.of(Long.valueOf(record.refId()), record.receiverId());
        given(emitterManager.isExistConnection(EmitterType.CHAT, chatKey)).willReturn(true);
        Message message = mock(Message.class);
        given(message.getBody()).willReturn(objectMapper.writeValueAsBytes(record));

        assertThatCode(() -> redisSubscriber.onMessage(message, null)).doesNotThrowAnyException();

        then(emitterManager).should().sendEvent(eq(EmitterType.CHAT), eq(chatKey), any(), argThat(event ->
            event.toString().contains("MIX")
        ));
    }

    @Test
    void 거래_알림_처리() throws Exception {
        RedisRecord record = TRADE_RECORD;
        NotiEmitterKey notiKey = NotiEmitterKey.of(record.receiverId());
        given(emitterManager.isExistConnection(EmitterType.NOTIFICATION, notiKey)).willReturn(true);
        Message message = mock(Message.class);
        given(message.getBody()).willReturn(objectMapper.writeValueAsBytes(record));

        assertThatCode(() -> redisSubscriber.onMessage(message, null)).doesNotThrowAnyException();
        then(emitterManager).should().sendEvent(eq(EmitterType.NOTIFICATION), eq(notiKey), any(), any());
    }

    @Test
    void ACK_처리() throws Exception {
        RedisRecord record = RedisRecord.builder()
            .type("CHAT")
            .refId("123")
            .childId("READ_ACK")
            .receiverId(UUID.randomUUID())
            .sender(RedisRecord.Sender.of(UUID.randomUUID(), "상대방", null))
            .sentAt(LocalDateTime.now())
            .build();

        ChatEmitterKey chatKey = ChatEmitterKey.of(Long.valueOf(record.refId()), record.receiverId());
        given(emitterManager.isExistConnection(EmitterType.CHAT, chatKey)).willReturn(false);

        Message message = mock(Message.class);
        given(message.getBody()).willReturn(objectMapper.writeValueAsBytes(record));

        assertThatCode(() -> redisSubscriber.onMessage(message, null)).doesNotThrowAnyException();
        then(emitterManager).shouldHaveNoMoreInteractions();
    }

    @Test
    void 역직렬화_실패_시_생략() {
        String invalidJson = "{ this is invalid json }";
        Message message = mock(Message.class);
        given(message.getBody()).willReturn(invalidJson.getBytes(StandardCharsets.UTF_8));

        assertThatCode(() -> redisSubscriber.onMessage(message, null)).doesNotThrowAnyException();
    }

    @Test
    void 알_수_없는_타입이면_생략() throws Exception {
        RedisRecord unknown = RedisRecord.builder()
            .type("UNKNOWN")
            .refId("999")
            .body("무시")
            .receiverId(UUID.randomUUID())
            .sentAt(LocalDateTime.now())
            .normalize(false)
            .build();

        Message message = mock(Message.class);
        given(message.getBody()).willReturn(objectMapper.writeValueAsBytes(unknown));

        assertThatCode(() -> redisSubscriber.onMessage(message, null)).doesNotThrowAnyException();
    }
}
