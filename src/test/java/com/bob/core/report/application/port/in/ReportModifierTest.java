package com.bob.core.report.application.port.in;

import static com.bob.core.chat.domain.type.ChatMessageType.TEXT;
import static com.bob.core.report.domain.ReportStatus.DUPLICATED;
import static com.bob.core.report.domain.ReportStatus.PROCESSED;
import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.bob.core.chat.domain.ChatMessage;
import com.bob.core.chat.domain.Chatroom;
import com.bob.core.chat.domain.repository.ChatroomRepository;
import com.bob.core.report.application.dto.command.ChangeReportStatusCommand;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.repository.ReportRepository;
import com.bob.core.report.event.ReportChatProcessedEvent;
import com.bob.core.report.event.ReportPostProcessedEvent;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.chat.domain.ChatroomFixture;
import com.bob.support.fixture.report.domain.ReportFixture;

@DisplayName("신고 수정 테스트")
@RequiredArgsConstructor
@ContainerTest
class ReportModifierTest {

    private final ReportModifier reportModifier;
    private final ReportRepository reportRepository;
    private final ChatroomRepository chatroomRepository;

    @MockitoBean
    private final ApplicationEventPublisher eventPublisher;

    @Test
    void 게시글_신고_처리() {
        Report report = reportRepository.save(ReportFixture.createInReviewReport());
        ChangeReportStatusCommand command = new ChangeReportStatusCommand(MANAGER_ID, "PROCESSED");

        Report result = reportModifier.changeStatus(report.getId(), command);

        assertThat(result.getStatus()).isEqualTo(PROCESSED);
        assertThat(result.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.getProcessedAt()).isNotNull();

        then(eventPublisher).should().publishEvent(any(ReportPostProcessedEvent.class));
    }

    @Test
    void 게시글_신고_처리_시_동일_게시글_신고는_중복_처리() {
        Report report1 = reportRepository.save(ReportFixture.createReport(1L));
        report1.review(MANAGER_ID);

        Report report2 = reportRepository.save(ReportFixture.createReport(1L));
        Report report3 = reportRepository.save(ReportFixture.createReport(1L));

        var command = new ChangeReportStatusCommand(MANAGER_ID, "PROCESSED");

        Report result = reportModifier.changeStatus(report1.getId(), command);

        assertThat(result.getStatus()).isEqualTo(PROCESSED);
        assertThat(result.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.getProcessedAt()).isNotNull();

        then(eventPublisher).should().publishEvent(any(ReportPostProcessedEvent.class));

        // 동일 대상 신고들은 중복 처리
        assertThat(report2.getStatus()).isEqualTo(DUPLICATED);
        assertThat(report3.getStatus()).isEqualTo(DUPLICATED);
    }

    @Test
    void 채팅_신고_처리() {
        Chatroom chatroom = chatroomRepository.save(ChatroomFixture.createChatroom());
        ChatMessage message = chatroom.addMessage(MEMBER_ID, "신고 대상 메시지", TEXT);
        chatroomRepository.flush();

        Report report = reportRepository.save(ReportFixture.createChatReport(message.getId()));
        report.review(MANAGER_ID);

        var command = new ChangeReportStatusCommand(MANAGER_ID, "PROCESSED");

        Report result = reportModifier.changeStatus(report.getId(), command);

        assertThat(result.getStatus()).isEqualTo(PROCESSED);
        then(eventPublisher).should().publishEvent(any(ReportChatProcessedEvent.class));
    }

    @Test
    void 채팅_신고_처리_시_동일_채팅방_신고는_중복_처리() {
        Chatroom chatroom = chatroomRepository.save(ChatroomFixture.createChatroom());
        ChatMessage message1 = chatroom.addMessage(MEMBER_ID, "메시지1", TEXT);
        ChatMessage message2 = chatroom.addMessage(MEMBER_ID, "메시지2", TEXT);
        chatroomRepository.flush();

        Report report1 = reportRepository.save(ReportFixture.createChatReport(message1.getId()));
        report1.review(MANAGER_ID);

        Report report2 = reportRepository.save(ReportFixture.createChatReport(message2.getId()));
        Report report3 = reportRepository.save(ReportFixture.createChatReport(message1.getId()));

        var command = new ChangeReportStatusCommand(MANAGER_ID, "PROCESSED");

        reportModifier.changeStatus(report1.getId(), command);

        assertThat(report2.getStatus()).isEqualTo(DUPLICATED);
        assertThat(report3.getStatus()).isEqualTo(DUPLICATED);

        then(eventPublisher).should().publishEvent(any(ReportChatProcessedEvent.class));
    }
}
