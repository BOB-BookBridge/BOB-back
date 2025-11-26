package com.bob.global.event.application.dto;

import java.util.UUID;

public record SystemChatMessageEvent(String domain, String refId, UUID memberId, UUID partnerId, String body) {

}
