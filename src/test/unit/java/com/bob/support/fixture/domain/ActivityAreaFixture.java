package com.bob.support.fixture.domain;

import static com.bob.support.fixture.domain.EmdAreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.area.entity.activity.ActivityArea;
import com.bob.domain.area.entity.activity.ActivityAreaId;
import java.time.LocalDate;
import java.util.UUID;

public class ActivityAreaFixture {

  public static ActivityAreaId defaultActivityAreaId() {
    return new ActivityAreaId(MEMBER_ID, EMD_AREA_ID);
  }

  public static ActivityArea defaultActivityArea() {
    return ActivityArea.builder()
        .id(new ActivityAreaId(MEMBER_ID, EMD_AREA_ID))
        .authenticationAt(LocalDate.now())
        .build();
  }

  public static ActivityArea customActivityArea(UUID memberId, Integer emdAreaId) {
    return ActivityArea.builder()
        .id(new ActivityAreaId(memberId, emdAreaId))
        .authenticationAt(LocalDate.now())
        .build();
  }

  public static ActivityArea customTimeActivityArea(UUID memberId, Integer emdAreaId, LocalDate date) {
    return ActivityArea.builder()
        .id(new ActivityAreaId(memberId, emdAreaId))
        .authenticationAt(date)
        .build();
  }
}