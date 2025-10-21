package com.bob.domain.member.service.dto.command;

import java.util.List;

public record FreeMemberBookUsageByIdsCommand(
    List<Long> ids
) {

  public static FreeMemberBookUsageByIdsCommand of(List<Long> ids) {
    return new FreeMemberBookUsageByIdsCommand(ids);
  }
}
