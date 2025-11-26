package com.bob.global.event.sse.repository.notification;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bob.global.event.sse.repository.EmitterRepository;

@RequiredArgsConstructor
@Component
public class NotificationEmitterRepository implements EmitterRepository<NotiEmitterKey> {

    private final Map<NotiEmitterKey, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(NotiEmitterKey key, SseEmitter emitter) {
        emitters.put(key, emitter);
        return emitter;
    }

    @Override
    public SseEmitter get(NotiEmitterKey key) {
        return emitters.get(key);
    }

    @Override
    public Map<NotiEmitterKey, SseEmitter> findAll() {
        return emitters;
    }

    @Override
    public void remove(NotiEmitterKey key) {
        emitters.remove(key);
    }

    @Override
    public boolean exists(NotiEmitterKey key) {
        return emitters.containsKey(key);
    }
}
