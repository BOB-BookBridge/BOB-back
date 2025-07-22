package com.bob.infra.redis.subscriber;

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

import com.bob.global.event.sse.manager.EmitterManager;
import com.bob.global.event.sse.manager.type.EmitterType;
import com.bob.global.event.sse.repository.chat.ChatEmitterKey;
import com.bob.global.event.sse.repository.notification.NotiEmitterKey;
import com.bob.infra.redis.record.RedisRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisSubscriber 테스트")
class RedisSubscriberTest {

  @InjectMocks
  private RedisSubscriber redisSubscriber;

  @Mock
  private EmitterManager emitterManager;

  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(redisSubscriber, "objectMapper", objectMapper);
  }

  @Test
  @DisplayName("Redis 메시지 수신 - 채팅 emitter 존재 시 채팅 메시지 전송")
  void 채팅_메시지_수신_성공_채팅_Emitter_존재() throws Exception {
    // given
    RedisRecord record = CHAT_TEXT_RECORD;
    ChatEmitterKey chatKey = ChatEmitterKey.of(Long.valueOf(record.refId()), record.receiverId());
    given(emitterManager.isExistClientConnection(EmitterType.CHAT, chatKey)).willReturn(true);

    Message message = mock(Message.class);
    given(message.getBody()).willReturn(objectMapper.writeValueAsBytes(record));

    // when & then
    assertThatCode(() -> redisSubscriber.onMessage(message, null)).doesNotThrowAnyException();
    then(emitterManager).should().sendEvent(eq(EmitterType.CHAT), eq(chatKey), any(), any());
  }

  @Test
  @DisplayName("Redis 메시지 수신 - 채팅 emitter 없으면 알림 전송")
  void 채팅_메시지_수신_성공_알림_전송() throws Exception {
    // given
    RedisRecord record = CHAT_TEXT_RECORD;
    ChatEmitterKey chatKey = ChatEmitterKey.of(Long.valueOf(record.refId()), record.receiverId());
    NotiEmitterKey notiKey = NotiEmitterKey.of(record.receiverId());
    given(emitterManager.isExistClientConnection(EmitterType.CHAT, chatKey)).willReturn(false);
    given(emitterManager.isExistClientConnection(EmitterType.NOTIFICATION, notiKey)).willReturn(true);
    Message message = mock(Message.class);
    given(message.getBody()).willReturn(objectMapper.writeValueAsBytes(record));

    // when & then
    assertThatCode(() -> redisSubscriber.onMessage(message, null)).doesNotThrowAnyException();

    then(emitterManager).should().sendEvent(eq(EmitterType.NOTIFICATION), eq(notiKey), any(), any());
  }

  @Test
  @DisplayName("Redis 메시지 수신 - 이미지 메시지 처리")
  void 이미지_메시지_수신_성공() throws Exception {
    // given
    RedisRecord record = CHAT_IMAGE_RECORD;
    ChatEmitterKey chatKey = ChatEmitterKey.of(Long.valueOf(record.refId()), record.receiverId());
    given(emitterManager.isExistClientConnection(EmitterType.CHAT, chatKey)).willReturn(true);
    Message message = mock(Message.class);
    given(message.getBody()).willReturn(objectMapper.writeValueAsBytes(record));

    // when & then
    assertThatCode(() -> redisSubscriber.onMessage(message, null)).doesNotThrowAnyException();

    then(emitterManager).should().sendEvent(eq(EmitterType.CHAT), eq(chatKey), any(), argThat(event ->
        event.toString().contains("IMAGE")
    ));
  }

  @Test
  @DisplayName("Redis 메시지 수신 - 혼합형 메시지 처리")
  void 혼합형_메시지_수신_성공() throws Exception {
    // given
    RedisRecord record = CHAT_MIX_RECORD;
    ChatEmitterKey chatKey = ChatEmitterKey.of(Long.valueOf(record.refId()), record.receiverId());
    given(emitterManager.isExistClientConnection(EmitterType.CHAT, chatKey)).willReturn(true);
    Message message = mock(Message.class);
    given(message.getBody()).willReturn(objectMapper.writeValueAsBytes(record));

    // when & then
    assertThatCode(() -> redisSubscriber.onMessage(message, null)).doesNotThrowAnyException();

    then(emitterManager).should().sendEvent(eq(EmitterType.CHAT), eq(chatKey), any(), argThat(event ->
        event.toString().contains("MIX")
    ));
  }

  @Test
  @DisplayName("Redis 메시지 수신 - 거래 알림 전송")
  void 거래_알림_수신_성공() throws Exception {
    // given
    RedisRecord record = TRADE_RECORD;
    NotiEmitterKey notiKey = NotiEmitterKey.of(record.receiverId());
    given(emitterManager.isExistClientConnection(EmitterType.NOTIFICATION, notiKey)).willReturn(true);
    Message message = mock(Message.class);
    given(message.getBody()).willReturn(objectMapper.writeValueAsBytes(record));

    // when & then
    assertThatCode(() -> redisSubscriber.onMessage(message, null)).doesNotThrowAnyException();
    then(emitterManager).should().sendEvent(eq(EmitterType.NOTIFICATION), eq(notiKey), any(), any());
  }

  @Test
  @DisplayName("Redis 메시지 수신 - READ_ACK 메시지의 경우 시스템 알림 전송 X")
  void READ_ACK_메시지는_알림을_전송하지_않는다() throws Exception {
    // given
    RedisRecord record = RedisRecord.builder()
        .type("CHAT")
        .refId("123")
        .childId("READ_ACK")
        .receiverId(UUID.randomUUID())
        .sender(RedisRecord.Sender.of(UUID.randomUUID(), "상대방", null))
        .sentAt(LocalDateTime.now())
        .build();

    ChatEmitterKey chatKey = ChatEmitterKey.of(Long.valueOf(record.refId()), record.receiverId());
    given(emitterManager.isExistClientConnection(EmitterType.CHAT, chatKey)).willReturn(false);

    Message message = mock(Message.class);
    given(message.getBody()).willReturn(objectMapper.writeValueAsBytes(record));

    // when & then
    assertThatCode(() -> redisSubscriber.onMessage(message, null)).doesNotThrowAnyException();
    then(emitterManager).shouldHaveNoMoreInteractions();
  }


  @Test
  @DisplayName("Redis 메시지 수신 - 역직렬화 실패")
  void 역직렬화_실패() {
    // given
    String invalidJson = "{ this is invalid json }";
    Message message = mock(Message.class);
    given(message.getBody()).willReturn(invalidJson.getBytes(StandardCharsets.UTF_8));

    // when & then
    assertThatCode(() -> redisSubscriber.onMessage(message, null)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Redis 메시지 수신 - 알 수 없는 타입 무시")
  void 알_수_없는_타입_무시() throws Exception {
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

    // when & then
    assertThatCode(() -> redisSubscriber.onMessage(message, null)).doesNotThrowAnyException();
  }
}
