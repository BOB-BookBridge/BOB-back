package com.bob.admin.inquiry.application.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.admin.inquiry.application.dto.command.ProcessManagementInquiryCommand;
import com.bob.core.inquiry.domain.Inquiry;
import com.bob.core.inquiry.domain.InquiryStatus;
import com.bob.core.inquiry.domain.repository.InquiryRepository;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.inquiry.domain.InquiryFixture;

@DisplayName("관리자 - 문의 처리 테스트")
@RequiredArgsConstructor
@ContainerTest
class ManagementInquiryProcessorTest {

    private final ManagementInquiryProcessor inquiryProcessor;
    private final InquiryRepository inquiryRepository;
    private final EntityManager em;

    @Test
    void 문의_상태_변경() {
        Inquiry inquiry = inquiryRepository.save(InquiryFixture.createInReviewInquiry());
        var command = new ProcessManagementInquiryCommand(MANAGER_ID, "PROCESSED", "답변");

        inquiryProcessor.process(inquiry.getId(), command);

        invalidPersistenceContext();

        Inquiry result = inquiryRepository.findById(inquiry.getId()).orElseThrow();
        assertThat(result.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.getStatus()).isEqualTo(InquiryStatus.PROCESSED);
        assertThat(result.getProcessedAt()).isNotNull();
        assertThat(result.getReply()).isEqualTo("답변");
    }

    private void invalidPersistenceContext() {
        em.flush();
        em.clear();
    }
}
