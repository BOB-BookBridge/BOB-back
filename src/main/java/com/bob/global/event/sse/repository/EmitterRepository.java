package com.bob.global.event.sse.repository;

import java.util.Map;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository<T> {

    SseEmitter save(T key, SseEmitter emitter);

    SseEmitter get(T key);

    Map<T, SseEmitter> findAll();

    void remove(T key);

    boolean exists(T key);
}
