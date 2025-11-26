package com.bob.core.application.chat.dto.command;

import java.util.UUID;

public record CreateSystemMessageCommand(String domain, String refId, UUID senderId, UUID partnerId, String body) {

}
