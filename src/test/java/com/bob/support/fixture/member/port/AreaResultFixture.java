package com.bob.support.fixture.member.port;

import static com.bob.support.fixture.area.domain.AreaFixture.EMD_AREA_ID;

import com.bob.core.application.member.port.result.MemberAreaResult;

public class AreaResultFixture {

    public static MemberAreaResult defaultAreaResult() {
        return MemberAreaResult.of(EMD_AREA_ID, "테스트동", "테스트구", "테스트시");
    }
}
