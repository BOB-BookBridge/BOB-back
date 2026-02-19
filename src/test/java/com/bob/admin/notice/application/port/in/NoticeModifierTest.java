package com.bob.admin.notice.application.port.in;

import static com.bob.support.fixture.notice.domain.NoticeFixture.createBannerNotice;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.admin.notice.domain.Notice;
import com.bob.admin.notice.domain.repository.NoticeRepository;
import com.bob.support.annotation.ContainerTest;

@DisplayName("공지 수정 테스트")
@ContainerTest
record NoticeModifierTest(NoticeModifier noticeModifier, NoticeRepository noticeRepository) {

    @Test
    void 활성화된_공지_비활성화() {
        noticeRepository.save(createBannerNotice());

        Notice updated = noticeModifier.deactivateCurrentBanner();

        assertThat(updated.getEndsAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void 활성화된_공지가_없으면_예외가_발생한다() {
        assertThatThrownBy(noticeModifier::deactivateCurrentBanner)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("활성화 되어있는 공지가 없습니다");
    }
}
