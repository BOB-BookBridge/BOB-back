package com.bob.admin.notice.adapter.api.response;

import java.time.LocalDateTime;

import com.bob.admin.notice.adapter.api.response.internal.NoticeWriter;
import com.bob.admin.notice.application.port.result.BannerNotice;

public record BannerNoticeResponse(String title, String content, LocalDateTime endTime, NoticeWriter writer) {

    public static BannerNoticeResponse of(BannerNotice notice) {
        return new BannerNoticeResponse(
            notice.title(), notice.content(), notice.endTime(),
            new NoticeWriter(notice.writerId(), notice.writerNickname())
        );
    }
}
