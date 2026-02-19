package com.bob.admin.notice.application.port.result;

import java.time.LocalDateTime;
import java.util.UUID;

public record AlertNotice(String title, String content, LocalDateTime createdAt, UUID writerId, String writerNickname) {

}
