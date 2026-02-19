package com.bob.admin.notice.adapter.api.response;

import java.time.LocalDateTime;

import com.bob.admin.notice.adapter.api.response.internal.NoticeWriter;
import com.bob.admin.notice.application.port.result.AlertNotice;

public record AlertNoticesResponse(NoticeWriter writer, String title, String content, LocalDateTime createdAt) {

    public static AlertNoticesResponse of(AlertNotice notice) {
        return new AlertNoticesResponse(
            new NoticeWriter(notice.writerId(), notice.writerNickname()),
            notice.title(),
            notice.content(),
            notice.createdAt()
        );
    }
}
