package com.bob.core.application.chat.dto.command;

import java.util.List;
import java.util.UUID;

import lombok.Builder;

@Builder
public record CreateMessageCommand(UUID memberId, String content, List<String> fileNames) {

}
