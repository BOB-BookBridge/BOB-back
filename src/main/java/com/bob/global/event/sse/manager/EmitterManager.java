package com.bob.global.event.sse.manager;

import static com.bob.global.event.sse.manager.type.EmitEventType.CONNECT;
import static com.bob.global.event.sse.manager.type.EmitEventType.HEARTBEAT;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bob.global.event.sse.manager.type.EmitEventType;
import com.bob.global.event.sse.manager.type.EmitterType;
import com.bob.global.event.sse.repository.EmitterRepository;
import com.bob.global.event.sse.repository.chat.ChatEmitterKey;
import com.bob.global.event.sse.repository.notification.NotiEmitterKey;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmitterManager implements Runnable {

    private final EmitterRepository<NotiEmitterKey> notificationEmitterRepository;
    private final EmitterRepository<ChatEmitterKey> chatEmitterRepository;
    private final ScheduledExecutorService sseHeartbeatScheduler;

    @Value("${sse.default-timeout}")
    private Long defaultTimeout;

    @Value("${sse.heartbeat-interval}")
    private Long heartbeatInterval;

    @PostConstruct
    public void init() {
        sseHeartbeatScheduler.scheduleAtFixedRate(this, 10, heartbeatInterval, TimeUnit.SECONDS);
    }

    public SseEmitter subscribeToChat(Long chatRoomId, UUID memberId) {
        return subscribe(EmitterType.CHAT, new ChatEmitterKey(chatRoomId, memberId));
    }

    public SseEmitter subscribeToNoti(UUID memberId) {
        return subscribe(EmitterType.NOTIFICATION, new NotiEmitterKey(memberId));
    }

    private <T> SseEmitter subscribe(EmitterType type, T key) {
        EmitterRepository<T> repository = getRepository(type);
        SseEmitter old = repository.get(key);
        if (old != null) {
            removeEmitter(old, key, repository);
        }

        SseEmitter emitter = new SseEmitter(defaultTimeout * 1000);
        setupEmitter(emitter, key, repository);
        repository.save(key, emitter);
        sendEvent(type, key, CONNECT, "connected");
        return emitter;
    }

    @Override
    public void run() {
        sendHeartbeatToRepository(notificationEmitterRepository);
        sendHeartbeatToRepository(chatEmitterRepository);
    }

    private <T> void sendHeartbeatToRepository(EmitterRepository<T> repository) {
        repository.findAll().forEach((key, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name(HEARTBEAT.name()).data("ping"));
            } catch (Exception e) {
                log.debug("Failed to send heartbeat for key: {}, removing emitter", key);
                removeEmitter(emitter, key, repository);
            }
        });
    }

    private <T> void setupEmitter(SseEmitter emitter, T key, EmitterRepository<T> repository) {
        emitter.onCompletion(() -> repository.remove(key));
        emitter.onTimeout(() -> repository.remove(key));
        emitter.onError(e -> repository.remove(key));
    }

    public <T> void sendEvent(EmitterType type, T key, EmitEventType eventType, Object data) {
        SseEmitter emitter = getEmitter(type, key);
        if (emitter == null)
            return;

        try {
            emitter.send(SseEmitter.event().name(eventType.name()).data(data));
        } catch (Exception e) {
            log.warn("Failed to send SSE event - key={}, event={}, error={}", key, eventType.name(), e.toString());
            removeEmitter(emitter, key, getRepository(type));
        }
    }

    public <T> boolean isExistConnection(EmitterType type, T key) {
        SseEmitter emitter = getEmitter(type, key);
        try {
            emitter.send(SseEmitter.event().name(HEARTBEAT.name()).data("ping"));
            return true;
        } catch (Exception e) {
            removeEmitter(emitter, key, getRepository(type));
            return false;
        }
    }

    private <T> void removeEmitter(SseEmitter emitter, T key, EmitterRepository<T> repository) {
        try {
            if (emitter != null) {
                emitter.complete();
            }
            repository.remove(key);
        } catch (Exception e) {
            log.debug("Error during emitter cleanup for key: {}", key);
        }
    }

    private <T> SseEmitter getEmitter(EmitterType type, T key) {
        return switch (type) {
            case CHAT -> chatEmitterRepository.get((ChatEmitterKey)key);
            case NOTIFICATION -> notificationEmitterRepository.get((NotiEmitterKey)key);
        };
    }

    @SuppressWarnings("unchecked")
    private <T> EmitterRepository<T> getRepository(EmitterType type) {
        return switch (type) {
            case CHAT -> (EmitterRepository<T>)chatEmitterRepository;
            case NOTIFICATION -> (EmitterRepository<T>)notificationEmitterRepository;
        };
    }
}
