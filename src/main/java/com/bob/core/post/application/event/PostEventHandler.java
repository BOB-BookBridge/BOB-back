package com.bob.core.post.application.event;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bob.core.member.event.MemberDeactivatedEvent;
import com.bob.core.member.event.MemberRecoveredEvent;
import com.bob.core.post.application.dto.command.ChangeMemberPostStatusCommand;
import com.bob.core.post.application.dto.command.ChangePostTradeProgressCommand;
import com.bob.core.post.application.port.in.PostModifier;
import com.bob.core.post.domain.status.Status;
import com.bob.core.trade.event.TradeStatusChangedEvent;

@Component
@RequiredArgsConstructor
public class PostEventHandler {

    private final PostModifier postModifier;

    @EventListener
    public void handleMemberDeactivated(MemberDeactivatedEvent event) {
        ChangeMemberPostStatusCommand command = new ChangeMemberPostStatusCommand(event.memberId(), Status.DEACTIVATED);

        postModifier.changeStatusByAccountEvent(command);
    }

    @EventListener
    public void handleMemberRecovered(MemberRecoveredEvent event) {
        ChangeMemberPostStatusCommand command = new ChangeMemberPostStatusCommand(event.memberId(), Status.ACTIVE);

        postModifier.changeStatusByAccountEvent(command);
    }

    @EventListener
    public void handleTradeStatusChanged(TradeStatusChangedEvent event) {
        ChangePostTradeProgressCommand command = new ChangePostTradeProgressCommand(event.newStatus());

        postModifier.changePostTradeProgress(event.postId(), command);
    }
}
