package com.bob.domain.notification.service.port;

import java.util.List;
import java.util.UUID;

public interface NotiRedisPort {

  void publish(
      UUID receiverId, String type, String refId, String body, List<String> fileNames,
      boolean normalize, UUID sId, String sNickname, String sProfile
  );
}
