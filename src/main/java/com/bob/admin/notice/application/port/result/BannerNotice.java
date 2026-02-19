package com.bob.admin.notice.application.port.result;

import java.time.LocalDateTime;
import java.util.UUID;

public record BannerNotice(String title, String content, LocalDateTime endTime, UUID writerId, String writerNickname) {

}
