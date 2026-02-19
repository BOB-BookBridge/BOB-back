package com.bob.core.notification.application.port.out;

import java.util.List;
import java.util.UUID;

import com.bob.core.notification.application.port.result.NotificationMember;

public interface NotificationMemberPort {

    NotificationMember read(UUID memberId);

    List<UUID> readAllMemberIds();
}
