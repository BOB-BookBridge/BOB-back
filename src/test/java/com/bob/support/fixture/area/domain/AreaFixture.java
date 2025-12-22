package com.bob.support.fixture.area.domain;

import com.bob.core.area.domain.EmdArea;
import com.bob.core.area.domain.SidoArea;
import com.bob.core.area.domain.SiggArea;
import com.bob.support.fixture.util.GeometryUtils;

public class AreaFixture {

    public static final int EMD_AREA_ID = 213;

    // 중심 좌표 (WGS84)
    public static final double CENTER_LAT = 37.5010;
    public static final double CENTER_LON = 127.0360;

    // 1km x 1km 정사각형, 반경 500m
    private static final double HALF_SIZE_METERS = 500.0;

    public static EmdArea createEmdArea() {
        return EmdArea.builder()
            .id(1)
            .siggArea(createSiggArea())
            .admCode("100")
            .name("테스트동")
            .geom(GeometryUtils.squareWgs84(CENTER_LAT, CENTER_LON, HALF_SIZE_METERS))
            .build();
    }

    public static SiggArea createSiggArea() {
        return SiggArea.builder()
            .id(1)
            .sidoArea(createSidoArea())
            .admCode("500")
            .name("테스트구")
            .build();
    }

    public static SidoArea createSidoArea() {
        return SidoArea.builder()
            .id(1)
            .admCode("10")
            .name("테스트시")
            .build();
    }
}
