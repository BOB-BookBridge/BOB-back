package com.bob.domain.notification.service.port;

import java.util.UUID;

public interface NotiRedisPort {

  void publish(
      UUID receiverId, String type, String refId, String body,
      boolean normalize, UUID sId, String sNickname, String sProfile
  );
}
