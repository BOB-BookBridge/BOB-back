package com.bob.infra.redis.adapter.in;

import com.bob.domain.notification.service.port.NotiRedisPort;
import com.bob.infra.redis.publisher.RedisPublisher;
import com.bob.infra.redis.record.RedisRecord;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotiRedisAdapter implements NotiRedisPort {

  private final RedisPublisher publisher;

  @Override
  public void publish(
      UUID rId, String type, String refId, String childId, String body, List<String> fileNames,
      boolean normalize, UUID sId, String sNickname, String sProfile
  ) {
    publisher.publish(RedisRecord.of(rId, type, refId, childId, body, fileNames, normalize, sId, sNickname, sProfile));
  }
}
