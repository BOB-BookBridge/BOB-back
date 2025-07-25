package com.bob.web.notification.controller;

import static com.bob.web.common.symbol.ResponseSymbol.*;

import com.bob.domain.notification.service.dto.command.ChangeNotificationReadStatusCommand;
import com.bob.domain.notification.service.dto.query.ReadNotificationsQuery;
import com.bob.domain.notification.service.dto.response.NotificationsResponse;
import com.bob.domain.notification.usecase.NotiModifyUseCase;
import com.bob.domain.notification.usecase.NotiReadUseCase;
import com.bob.web.common.AuthenticationId;
import com.bob.web.common.CommonResponse;
import com.bob.web.common.symbol.ResponseSymbol;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotiController {

  private final NotiReadUseCase readUseCase;
  private final NotiModifyUseCase modifyUseCase;

  @GetMapping
  public ResponseEntity<NotificationsResponse> handleReadNotifications(
      @AuthenticationId UUID memberId
  ) {
    return ResponseEntity.ok(readUseCase.readNotificationsProcess(ReadNotificationsQuery.of(memberId)));
  }

  @PatchMapping("/{notificationId}")
  public CommonResponse<ResponseSymbol> handleModifyNotificationReadStatus(
      @PathVariable Long notificationId,
      @AuthenticationId UUID memberId
  ) {
    modifyUseCase.changeNotificationReadStatusProcess(ChangeNotificationReadStatusCommand.of(memberId, notificationId));
    return new CommonResponse<>(true, UPDATED);
  }
}
