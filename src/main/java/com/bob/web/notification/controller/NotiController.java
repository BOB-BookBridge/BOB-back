package com.bob.web.notification.controller;

import com.bob.domain.notification.service.dto.query.ReadNotificationsQuery;
import com.bob.domain.notification.service.dto.response.NotificationsResponse;
import com.bob.domain.notification.usecase.NotiReadUseCase;
import com.bob.web.common.AuthenticationId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotiController {

  private final NotiReadUseCase readUseCase;

  @GetMapping
  public ResponseEntity<NotificationsResponse> handleReadNotifications(
      @AuthenticationId UUID memberId
  ) {
    return ResponseEntity.ok(readUseCase.readNotificationsProcess(ReadNotificationsQuery.of(memberId)));
  }
}
