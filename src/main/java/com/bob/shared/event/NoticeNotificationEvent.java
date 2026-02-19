package com.bob.shared.event;

import java.util.UUID;

public record NoticeNotificationEvent(Long noticeId, UUID writerId) {

}
