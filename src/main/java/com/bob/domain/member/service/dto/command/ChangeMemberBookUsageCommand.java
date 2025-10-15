package com.bob.domain.member.service.dto.command;

import java.util.List;
import java.util.UUID;

public record ChangeMemberBookUsageCommand(
    UUID memberId,
    Long usageId,
    List<Long> memberBookIds,
    boolean release
) {

  public static ChangeMemberBookUsageCommand of(UUID memberId, Long usageId, List<Long> memberBookIds, boolean release) {
    return new ChangeMemberBookUsageCommand(memberId, usageId, memberBookIds, release);
  }
}
