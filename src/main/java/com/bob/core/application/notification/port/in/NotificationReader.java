package com.bob.core.application.notification.port.in;

import java.util.List;

import com.bob.core.application.notification.dto.query.ReadByMemberQuery;
import com.bob.core.domain.notification.Notification;

public interface NotificationReader {

    Notification read(Long id);

    List<Notification> readByMember(ReadByMemberQuery query);
}
