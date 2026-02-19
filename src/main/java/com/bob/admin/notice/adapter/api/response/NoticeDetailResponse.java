package com.bob.admin.notice.adapter.api.response;

import java.time.LocalDateTime;

import com.bob.admin.notice.adapter.api.response.internal.NoticeWriter;
import com.bob.admin.notice.application.port.result.NoticeDetail;

public record NoticeDetailResponse(NoticeWriter writer, String title, String content, LocalDateTime createdAt) {

    public static NoticeDetailResponse of(NoticeDetail notice) {
        return new NoticeDetailResponse(
            new NoticeWriter(notice.writerId(), notice.writerNickname()),
            notice.title(),
            notice.content(),
            notice.createdAt()
        );
    }
}
