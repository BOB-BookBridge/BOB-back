package com.bob.infra.redis.subscriber;

import static com.bob.global.event.sse.manager.type.EmitEventType.CHAT_MESSAGE;
import static com.bob.global.event.sse.manager.type.EmitEventType.NOTIFICATION;

import com.bob.global.event.sse.manager.EmitterManager;
import com.bob.global.event.sse.manager.dto.ChatMessageEmitEvent;
import com.bob.global.event.sse.manager.dto.NotiEmitEvent;
import com.bob.global.event.sse.manager.dto.NotiEmitEvent.Sender;
import com.bob.global.event.sse.manager.type.EmitEventType;
import com.bob.global.event.sse.manager.type.EmitterType;
import com.bob.global.event.sse.repository.chat.ChatEmitterKey;
import com.bob.global.event.sse.repository.notification.NotiEmitterKey;
import com.bob.infra.redis.record.RedisRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisSubscriber implements MessageListener {

  private final EmitterManager emitterManager;
  private final ObjectMapper objectMapper;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    try {
      String body = new String(message.getBody());
      RedisRecord record = objectMapper.readValue(body, RedisRecord.class);

      log.debug("Received RedisRecord - type: {}, refId: {}", record.type(), record.refId());

      switch (record.type()) {
        case "CHAT" -> handleChatEvent(record);
        case "TRADE" -> handleTradeEvent(record);
        default -> log.warn("Unknown RedisRecord type: {}", record.type());
      }

    } catch (Exception e) {
      log.error("Failed to process Redis message", e);
    }
  }

  private void handleChatEvent(RedisRecord record) {
    if (sendChatMessage(record)) {
      return;
    }
    sendChatNoti(record);
  }

  private boolean sendChatMessage(RedisRecord record) {
    ChatEmitterKey chatKey = ChatEmitterKey.of(Long.valueOf(record.refId()), record.receiverId());
    ChatMessageEmitEvent event = ChatMessageEmitEvent.of(record.isSystem(), record.childId(), record.body(), record.fileNames(), record.sentAt());
    boolean sent = notify(EmitterType.CHAT, chatKey, CHAT_MESSAGE, event);
    logResult("NOTI_CHAT_MESSAGE", sent, record.refId(), record.receiverId());
    return sent;
  }

  private void sendChatNoti(RedisRecord record) {
    NotiEmitterKey notiKey = NotiEmitterKey.of(record.receiverId());
    Sender sender = Sender.of(record.sender().id(), record.sender().nickname(), record.sender().profile());
    String body = record.normalize() ? "사진" : record.body();
    NotiEmitEvent event = NotiEmitEvent.of(record.type(), record.refId(), body, sender, record.sentAt());
    boolean sent = notify(EmitterType.NOTIFICATION, notiKey, NOTIFICATION, event);
    logResult("NOTI_CHAT", sent, record.refId(), record.receiverId());
  }

  private void handleTradeEvent(RedisRecord record) {
    NotiEmitterKey notiKey = NotiEmitterKey.of(record.receiverId());
    Sender sender = Sender.of(record.sender().id(), record.sender().nickname(), record.sender().profile());
    NotiEmitEvent event = NotiEmitEvent.of(record.type(), record.refId(), record.body(), sender, record.sentAt());
    boolean sent = notify(EmitterType.NOTIFICATION, notiKey, NOTIFICATION, event);
    logResult("NOTI_TRADE", sent, record.refId(), record.receiverId());
  }

  private <T> boolean notify(EmitterType type, T key, EmitEventType event, Object payload) {
    if (!emitterManager.isExistClientConnection(type, key)) {
      return false;
    }
    emitterManager.sendEvent(type, key, event, payload);
    return true;
  }

  private void logResult(String context, boolean success, String refId, Object receiverId) {
    if (success) {
      log.debug("[{}] SSE event sent successfully. refId={}, receiver={}", context, refId, receiverId);
    } else {
      log.info("[{}] No active SSE emitter. Event skipped. refId={}, receiver={}", context, refId, receiverId);
    }
  }
}
