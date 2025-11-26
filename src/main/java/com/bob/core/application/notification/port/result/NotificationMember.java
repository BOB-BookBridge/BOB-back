package com.bob.core.application.notification.port.result;

import java.util.UUID;

public record NotificationMember(UUID id, String nickname, String profileImageUrl) {

}
