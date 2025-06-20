package com.bob.support.fixture.domain;

import com.bob.domain.area.entity.EmdArea;
import com.bob.domain.area.entity.SiggArea;

public class EmdAreaFixture {

  public static final Integer EMD_AREA_ID = 213;
  public static final Integer SIGG_AREA_ID = 100;

  public static EmdArea defaultEmdArea() {
    return EmdArea.builder()
        .id(EMD_AREA_ID)
        .admCode("640")
        .name("역삼동")
        .siggArea(defaultSiggArea())
        .geom(null)
        .build();
  }

  public static SiggArea defaultSiggArea() {
    return SiggArea.builder()
        .id(SIGG_AREA_ID)
        .admCode("25")
        .name("노원구")
        .sidoArea(null)
        .build();
  }
}
