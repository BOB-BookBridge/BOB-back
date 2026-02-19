package com.bob.admin.notice.application.dto.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisterBannerCommand(UUID writerId, String content, LocalDateTime endTime) {

}
