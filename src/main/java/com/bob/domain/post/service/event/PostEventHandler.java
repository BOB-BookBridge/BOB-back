package com.bob.domain.post.service.event;

import com.bob.domain.post.entity.status.Status;
import com.bob.domain.post.service.PostService;
import com.bob.domain.post.service.dto.command.ChangeMemberPostStatusCommand;
import com.bob.global.event.application.dto.member.AccountEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostEventHandler {

  private final PostService postService;

  @EventListener
  public void handleAccountEvent(AccountEvent event) {
    ChangeMemberPostStatusCommand command = switch (event.type()) {
      case RECOVER -> new ChangeMemberPostStatusCommand(event.memberId(), Status.ACTIVE);
      case WITHDRAW -> new ChangeMemberPostStatusCommand(event.memberId(), Status.REMOVED);
    };

    postService.changeStatusByAccountEventProcess(command);
  }
}
