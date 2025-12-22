package com.bob.integration.adapter.post;

import static com.bob.support.fixture.area.domain.AreaFixture.createEmdArea;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bob.core.area.application.port.in.AreaReader;
import com.bob.core.area.domain.EmdArea;
import com.bob.core.post.application.port.result.PostArea;

@ExtendWith(MockitoExtension.class)
class PostAreaAdapterTest {

    @InjectMocks
    private PostAreaAdapter postAreaAdapter;

    @Mock
    private AreaReader areaReader;

    @Test
    void 지역_정보_조회() {
        int id = 1;
        EmdArea emdArea = createEmdArea();
        given(areaReader.read(id)).willReturn(emdArea);

        PostArea result = postAreaAdapter.read(id);

        assertThat(result.emdId()).isEqualTo(id);
        assertThat(result.emdName()).isEqualTo("테스트동");
        assertThat(result.siggName()).isEqualTo("테스트구");
    }
}
