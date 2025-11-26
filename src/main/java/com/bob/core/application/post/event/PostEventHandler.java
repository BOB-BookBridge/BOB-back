package com.bob.core.application.post.event;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bob.core.application.post.dto.command.ChangeMemberPostStatusCommand;
import com.bob.core.application.post.port.in.PostModifier;
import com.bob.core.domain.post.status.Status;
import com.bob.global.event.application.dto.member.AccountEvent;

@Component
@RequiredArgsConstructor
public class PostEventHandler {

    private final PostModifier postModifier;

    @EventListener
    public void handleAccountEvent(AccountEvent event) {
        ChangeMemberPostStatusCommand command = switch (event.type()) {
            case RECOVER -> new ChangeMemberPostStatusCommand(event.memberId(), Status.ACTIVE);
            case DEACTIVATE -> new ChangeMemberPostStatusCommand(event.memberId(), Status.DEACTIVATED);
        };

        postModifier.changeStatusByAccountEvent(command);
    }
}
