package com.bob.admin.notice.domain;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("공지 도메인 테스트")
class NoticeTest {

    @Test
    void 게시용_공지_생성() {
        Notice notice = Notice.createBanner(MANAGER_ID, "content", null);

        assertThat(notice.getType()).isEqualTo(NoticeType.BANNER);
        assertThat(notice.getTitle()).isEqualTo("[공지사항]");
    }

    @Test
    void 알림용_공지_생성() {
        Notice notice = Notice.createAlert(MANAGER_ID, "title", "content");

        assertThat(notice.getType()).isEqualTo(NoticeType.ALERT);
        assertThat(notice.getEndsAt()).isNull();

        Notice nonTitleNotice = Notice.createAlert(MANAGER_ID, null, "content");
        assertThat(nonTitleNotice.getTitle()).isEqualTo("[공지]");
    }

    @Test
    void 공지_생성_시_내용이_없으면_예외가_발생한다() {
        assertThatThrownBy(() -> Notice.createBanner(MANAGER_ID, null, LocalDateTime.now().plusHours(1)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("내용은 필수입니다");

        assertThatThrownBy(() -> Notice.createAlert(MANAGER_ID, "title", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("내용은 필수입니다");
    }

    @Test
    void 게시용_공지_생성_시_종료시각이_현재시각_이전이면_예외가_발생한다() {
        assertThatThrownBy(() -> Notice.createBanner(MANAGER_ID, "content", LocalDateTime.now().minusMinutes(1)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("게시 종료 시각은 현재 시각 이후여야 합니다");
    }

    @Test
    void 공지_비활성화() {
        Notice notice = Notice.createBanner(MANAGER_ID, "content", LocalDateTime.now().plusHours(1));
        assertThat(notice.getEndsAt()).isAfter(LocalDateTime.now());

        notice.deactivate();

        assertThat(notice.getEndsAt()).isNotNull();
    }
}
