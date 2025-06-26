package com.bob.web.dummy.command;

import com.bob.domain.area.entity.activity.ActivityArea;
import com.bob.domain.area.entity.activity.ActivityAreaId;
import com.bob.domain.member.entity.Member;
import java.time.LocalDate;
import java.util.UUID;

public record CreateDummyManagerCommand(
) {

  public Member toDummyManager(String encodedPassword) {
    return Member.builder()
        .email("manager@manager.com")
        .password(encodedPassword)
        .nickname("manager")
        .build();
  }

  public ActivityArea toActivityArea(UUID memberId) {
    return ActivityArea.builder()
        .id(new ActivityAreaId(memberId, 213))
        .authenticationAt(LocalDate.now())
        .build();
  }
}
