package com.bob.core.notification.adapter.api;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bob.core.shared.web.AuthenticationId;
import com.bob.global.event.sse.manager.EmitterManager;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationStreamApi {

    private final EmitterManager emitterManager;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@AuthenticationId UUID memberId) {
        return emitterManager.subscribeToNoti(memberId);
    }
}
