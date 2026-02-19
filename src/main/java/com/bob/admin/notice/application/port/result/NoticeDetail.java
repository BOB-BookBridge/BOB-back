package com.bob.admin.notice.application.port.result;

import java.time.LocalDateTime;
import java.util.UUID;

public record NoticeDetail(
    UUID writerId,
    String writerNickname,

    String title,
    String content,
    LocalDateTime createdAt
) {

}
