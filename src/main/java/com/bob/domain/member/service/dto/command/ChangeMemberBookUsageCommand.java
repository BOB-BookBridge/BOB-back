package com.bob.domain.member.service.dto.command;

import java.util.List;

public record ChangeMemberBookUsageCommand(
    Long usageId,
    List<Long> memberBookIds
) {

  public static ChangeMemberBookUsageCommand of(Long usageId, List<Long> memberBookIds) {
    return new ChangeMemberBookUsageCommand(usageId, memberBookIds);
  }
}
