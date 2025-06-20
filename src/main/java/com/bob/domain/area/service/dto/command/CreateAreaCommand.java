package com.bob.domain.area.service.dto.command;

import com.bob.domain.area.entity.activity.ActivityArea;
import com.bob.domain.area.entity.activity.ActivityAreaId;
import java.time.LocalDate;
import java.util.UUID;

public record CreateAreaCommand(
    UUID memberId,
    Integer emdId
) {

  public static CreateAreaCommand of(UUID memberId, Integer emdId) {
    return new CreateAreaCommand(memberId, emdId);
  }

  public ActivityArea toActivityArea() {
    return ActivityArea.builder()
        .id(new ActivityAreaId(memberId, emdId))
        .authenticationAt(LocalDate.now())
        .build();
  }
}
