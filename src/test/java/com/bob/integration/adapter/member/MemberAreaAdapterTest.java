package com.bob.integration.adapter.member;

import static com.bob.support.fixture.area.domain.AreaFixture.createEmdArea;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bob.core.area.application.port.in.AreaReader;
import com.bob.core.area.domain.EmdArea;
import com.bob.core.member.application.port.result.MemberAreaResult;

@DisplayName("회원 지역 adapter 테스트")
@ExtendWith(MockitoExtension.class)
class MemberAreaAdapterTest {

    @InjectMocks
    private MemberAreaAdapter memberAreaAdapter;

    @Mock
    private AreaReader areaReader;

    @Test
    void 지역_정보_조회() {
        int id = 1;
        EmdArea emdArea = createEmdArea();
        given(areaReader.read(id)).willReturn(emdArea);

        MemberAreaResult result = memberAreaAdapter.read(id);

        // then
        assertThat(result.emdId()).isEqualTo(id);
        assertThat(result.emdName()).isEqualTo("테스트동");
        assertThat(result.siggName()).isEqualTo("테스트구");
        assertThat(result.sidoName()).isEqualTo("테스트시");
    }
}
