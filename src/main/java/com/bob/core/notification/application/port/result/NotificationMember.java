package com.bob.core.notification.application.port.result;

import java.util.UUID;

public record NotificationMember(UUID id, String nickname, String profileImageUrl) {

}
