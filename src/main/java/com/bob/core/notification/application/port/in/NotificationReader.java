package com.bob.core.notification.application.port.in;

import java.util.List;

import com.bob.core.notification.application.dto.query.ReadByMemberQuery;
import com.bob.core.notification.domain.Notification;

public interface NotificationReader {

    Notification read(Long id);

    List<Notification> readByMember(ReadByMemberQuery query);
}
