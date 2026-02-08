package com.bob.core.post.application.event;

import lombok.RequiredArgsConstructor;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import com.bob.core.member.event.MemberDeactivatedEvent;
import com.bob.core.member.event.MemberRecoveredEvent;
import com.bob.core.post.application.dto.command.ChangeMemberPostStatusCommand;
import com.bob.core.post.application.dto.command.ChangePostTradeProgressCommand;
import com.bob.core.post.application.port.in.PostModifier;
import com.bob.core.post.application.port.in.PostReader;
import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.status.Status;
import com.bob.core.report.event.ReportPostProcessedEvent;
import com.bob.core.trade.event.TradeStatusChangedEvent;

@Component
@RequiredArgsConstructor
public class PostEventHandler {

    private final PostReader postReader;
    private final PostModifier postModifier;

    @ApplicationModuleListener
    public void handleMemberDeactivated(MemberDeactivatedEvent event) {
        ChangeMemberPostStatusCommand command = new ChangeMemberPostStatusCommand(event.memberId(), Status.DEACTIVATED);

        postModifier.changeStatusByAccountEvent(command);
    }

    @ApplicationModuleListener
    public void handleMemberRecovered(MemberRecoveredEvent event) {
        ChangeMemberPostStatusCommand command = new ChangeMemberPostStatusCommand(event.memberId(), Status.ACTIVE);

        postModifier.changeStatusByAccountEvent(command);
    }

    @ApplicationModuleListener
    public void handleTradeStatusChanged(TradeStatusChangedEvent event) {
        ChangePostTradeProgressCommand command = new ChangePostTradeProgressCommand(event.newStatus());

        postModifier.changePostTradeProgress(event.postId(), command);
    }

    @ApplicationModuleListener
    public void handlePostReportProcessed(ReportPostProcessedEvent event) {
        Post post = postReader.read(event.targetId());

        post.ban();
    }
}
