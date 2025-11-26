package com.bob.core.application.notification.port.out;

import java.util.UUID;

import com.bob.core.application.notification.port.result.NotificationMember;

public interface NotificationMemberPort {

    NotificationMember read(UUID memberId);
}
