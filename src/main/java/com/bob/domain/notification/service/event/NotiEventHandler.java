package com.bob.domain.notification.service.event;

import com.bob.domain.notification.service.NotiService;
import com.bob.domain.notification.service.dto.command.CreateNotiCommand;
import com.bob.global.event.application.dto.NotiEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotiEventHandler {

  private final NotiService notiService;

  @Async
  @EventListener
  public void handleNotiEvent(NotiEvent event) {
    CreateNotiCommand command = CreateNotiCommand.of(
        event.type(), event.refId(),
        event.senderId(), event.receiverId(), event.body(), event.normalize()
    );
    notiService.createNotificationProcess(command);
  }
}
