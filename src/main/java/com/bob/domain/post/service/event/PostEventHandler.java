package com.bob.domain.post.service.event;

import com.bob.domain.post.service.PostService;
import com.bob.domain.post.service.dto.command.WithholdPostStatusCommand;
import com.bob.global.event.application.dto.RemoveMemberEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostEventHandler {

  private final PostService postService;

  @EventListener
  public void handleMemberRemoveEvent(RemoveMemberEvent event) {
    WithholdPostStatusCommand command = new WithholdPostStatusCommand(event.memberId());
    postService.withholdPostProcess(command);
  }
}
