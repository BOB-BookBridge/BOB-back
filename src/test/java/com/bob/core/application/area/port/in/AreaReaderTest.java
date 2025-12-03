package com.bob.core.application.area.port.in;

import static com.bob.support.fixture.area.domain.AreaFixture.EMD_AREA_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.domain.area.EmdArea;
import com.bob.core.domain.area.SidoArea;
import com.bob.core.domain.area.SiggArea;
import com.bob.support.annotation.ContainerTest;

@DisplayName("지역 조회 테스트")
@ContainerTest
record AreaReaderTest(AreaReader areaReader) {

    @Test
    void 지역_조회() {
        EmdArea emdArea = areaReader.read(EMD_AREA_ID);

        SiggArea siggArea = emdArea.getSiggArea();
        SidoArea sidoArea = siggArea.getSidoArea();

        assertThat(emdArea.getId()).isEqualTo(EMD_AREA_ID);
        assertThat(emdArea.getName()).isEqualTo("역삼동");
        assertThat(siggArea.getName()).isEqualTo("강남구");
        assertThat(sidoArea.getName()).isEqualTo("서울특별시");
    }

    @Test
    void 지역_조회_시_존재하지_않으면_사용자_예외가_발생한다() {
        int invalidEmdAreaId = -1;

        assertThatThrownBy(() -> areaReader.read(invalidEmdAreaId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("지역을 찾을 수 없습니다.");
    }
}
