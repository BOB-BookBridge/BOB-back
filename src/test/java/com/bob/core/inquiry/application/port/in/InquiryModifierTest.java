package com.bob.core.inquiry.application.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.bob.core.inquiry.application.dto.command.ChangeInquiryStatusCommand;
import com.bob.core.inquiry.application.port.out.infra.InquiryMailSender;
import com.bob.core.inquiry.domain.Inquiry;
import com.bob.core.inquiry.domain.InquiryStatus;
import com.bob.core.inquiry.domain.repository.InquiryRepository;
import com.bob.core.inquiry.event.InquiryProcessedEvent;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.inquiry.domain.InquiryFixture;

@DisplayName("신고 처리 테스트")
@RequiredArgsConstructor
@ContainerTest
class InquiryModifierTest {

    final InquiryModifier inquiryModifier;
    final InquiryRepository inquiryRepository;

    @MockitoBean
    final ApplicationEventPublisher eventPublisher;

    @MockitoBean
    final InquiryMailSender mailSender;

    @Test
    void 문의_검토() {
        Inquiry inquiry = inquiryRepository.save(InquiryFixture.createInquiry());
        ChangeInquiryStatusCommand command = new ChangeInquiryStatusCommand(MANAGER_ID, InquiryStatus.IN_REVIEW, null);

        Inquiry result = inquiryModifier.changeStatus(inquiry.getId(), command);

        assertThat(result.getStatus()).isEqualTo(InquiryStatus.IN_REVIEW);
        assertThat(result.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.getProcessedAt()).isNull();
        assertThat(result.getReply()).isNull();

        then(eventPublisher).shouldHaveNoInteractions();
        then(mailSender).shouldHaveNoInteractions();
    }

    @Test
    void 문의_취소() {
        Inquiry inquiry = inquiryRepository.save(InquiryFixture.createInReviewInquiry());
        ChangeInquiryStatusCommand command = new ChangeInquiryStatusCommand(MANAGER_ID, InquiryStatus.CLOSED, null);

        Inquiry result = inquiryModifier.changeStatus(inquiry.getId(), command);

        assertThat(result.getStatus()).isEqualTo(InquiryStatus.CLOSED);
        assertThat(result.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.getProcessedAt()).isNotNull();
        assertThat(result.getReply()).isNull();

        then(eventPublisher).shouldHaveNoInteractions();
        then(mailSender).shouldHaveNoInteractions();
    }

    @Test
    void 회원_문의_처리() {
        Inquiry inquiry = inquiryRepository.save(InquiryFixture.createInReviewInquiry());
        ChangeInquiryStatusCommand command = new ChangeInquiryStatusCommand(MANAGER_ID, InquiryStatus.PROCESSED, "답변");

        Inquiry result = inquiryModifier.changeStatus(inquiry.getId(), command);

        assertThat(result.getStatus()).isEqualTo(InquiryStatus.PROCESSED);
        assertThat(result.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.getProcessedAt()).isNotNull();
        assertThat(result.getReply()).isEqualTo("답변");

        then(eventPublisher).should().publishEvent(any(InquiryProcessedEvent.class));
        then(mailSender).shouldHaveNoInteractions();
    }

    @Test
    void 비회원_문의_처리() {
        Inquiry inquiry = inquiryRepository.save(InquiryFixture.createInquiry("anonymous@test.com"));
        inquiry.review(MANAGER_ID);

        ChangeInquiryStatusCommand command = new ChangeInquiryStatusCommand(MANAGER_ID, InquiryStatus.PROCESSED, "답변");

        Inquiry result = inquiryModifier.changeStatus(inquiry.getId(), command);

        assertThat(result.getStatus()).isEqualTo(InquiryStatus.PROCESSED);
        assertThat(result.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.getProcessedAt()).isNotNull();
        assertThat(result.getReply()).isEqualTo("답변");

        then(eventPublisher).shouldHaveNoInteractions();
        then(mailSender).should().sendInquiryReply(eq(result.getEmail()), eq(result.getContent()), eq("답변"));
    }
}
