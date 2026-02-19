package com.bob.support.fixture.notice.domain;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;

import java.time.LocalDateTime;

import com.bob.admin.notice.domain.Notice;
import com.bob.admin.notice.domain.NoticeType;

public class NoticeFixture {

    public static Notice createBannerNotice() {
        return Notice.builder()
            .type(NoticeType.BANNER)
            .writerId(MANAGER_ID)
            .title("[공지사항]")
            .content("content")
            .endsAt(null)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static Notice createAlertNotice() {
        return Notice.builder()
            .type(NoticeType.ALERT)
            .writerId(MANAGER_ID)
            .title("[title]")
            .content("content")
            .endsAt(null)
            .createdAt(LocalDateTime.now())
            .build();
    }
}
