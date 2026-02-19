package com.bob.admin.notice.application.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.admin.notice.application.port.result.AlertNotice;
import com.bob.admin.notice.application.port.result.BannerNotice;
import com.bob.admin.notice.application.port.result.NoticeDetail;
import com.bob.admin.notice.domain.Notice;
import com.bob.admin.notice.domain.repository.NoticeRepository;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.notice.domain.NoticeFixture;

@DisplayName("공지 조회 테스트")
@ContainerTest
record NoticeReaderTest(NoticeReader noticeReader, NoticeRepository noticeRepository, EntityManager em) {

    @Test
    void 공지_조회() {
        Notice saved = noticeRepository.save(Notice.createAlert(MANAGER_ID, "title", "content"));

        em.flush();
        em.clear();

        Notice result = noticeReader.read(saved.getId());

        assertThat(result.getId()).isEqualTo(saved.getId());
        assertThat(result.getType()).isEqualTo(saved.getType());
        assertThat(result.getWriterId()).isEqualTo(MANAGER_ID);
    }

    @Test
    void 공지_조회_시_존재하지_않으면_예외가_발생한다() {
        assertThatThrownBy(() -> noticeReader.read(999L))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 공지_상세_조회() {
        Notice saved = noticeRepository.save(Notice.createAlert(MANAGER_ID, "title", "content"));

        em.flush();
        em.clear();

        NoticeDetail detail = noticeReader.readDetail(saved.getId());

        assertThat(detail.writerId()).isEqualTo(MANAGER_ID);
        assertThat(detail.writerNickname()).isNotBlank();
        assertThat(detail.title()).isEqualTo("[title]");
        assertThat(detail.content()).isEqualTo("content");
        assertThat(detail.createdAt()).isNotNull();
    }

    @Test
    void 게시용_공지_조회() {
        noticeRepository.save(Notice.createBanner(MANAGER_ID, "old", LocalDateTime.now().plusHours(2)));
        noticeRepository.save(Notice.createBanner(MANAGER_ID, "new", LocalDateTime.now().plusHours(1)));

        em.flush();
        em.clear();

        BannerNotice banner = noticeReader.readBanner().orElseThrow();

        assertThat(banner.title()).isEqualTo("[공지사항]");
        assertThat(banner.content()).isEqualTo("new");
        assertThat(banner.writerId()).isEqualTo(MANAGER_ID);
        assertThat(banner.writerNickname()).isNotBlank();
    }

    @Test
    void 게시용_공지_없으면_빈값_반환() {
        assertThat(noticeReader.readBanner()).isEmpty();
    }

    @Test
    void 알림용_공지_목록_조회() {
        noticeRepository.save(NoticeFixture.createAlertNotice());
        noticeRepository.save(NoticeFixture.createAlertNotice());
        noticeRepository.save(NoticeFixture.createBannerNotice());
        noticeRepository.save(NoticeFixture.createAlertNotice());

        em.flush();
        em.clear();

        List<AlertNotice> notices = noticeReader.readCurrentAlertNotices();

        assertThat(notices).hasSize(3);
    }
}
