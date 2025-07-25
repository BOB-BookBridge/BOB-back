package com.bob.domain.notification.usecase;

import com.bob.domain.notification.service.dto.command.ChangeNotificationReadStatusCommand;

public interface NotiModifyUseCase {

  void changeNotificationReadStatusProcess(ChangeNotificationReadStatusCommand command);
}
