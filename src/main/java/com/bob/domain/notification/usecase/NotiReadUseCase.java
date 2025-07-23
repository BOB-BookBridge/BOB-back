package com.bob.domain.notification.usecase;

import com.bob.domain.notification.service.dto.query.ReadNotificationsQuery;
import com.bob.domain.notification.service.dto.response.NotificationsResponse;

public interface NotiReadUseCase {
  NotificationsResponse readNotificationsProcess(ReadNotificationsQuery query);
}
