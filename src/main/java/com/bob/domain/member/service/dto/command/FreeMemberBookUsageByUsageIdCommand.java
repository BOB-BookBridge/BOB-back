package com.bob.domain.member.service.dto.command;

public record FreeMemberBookUsageByUsageIdCommand(
    Long usageId
) {

  public static FreeMemberBookUsageByUsageIdCommand of(Long usageId) {
    return new FreeMemberBookUsageByUsageIdCommand(usageId);
  }
}
