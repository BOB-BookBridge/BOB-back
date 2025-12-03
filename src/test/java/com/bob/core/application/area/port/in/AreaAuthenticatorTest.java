package com.bob.core.application.area.port.in;

import static com.bob.global.exception.response.ApplicationError.AREA_AUTHENTICATION_FAILED;
import static com.bob.support.fixture.area.domain.AreaFixture.CENTER_LAT;
import static com.bob.support.fixture.area.domain.AreaFixture.CENTER_LON;
import static com.bob.support.fixture.area.domain.AreaFixture.createEmdArea;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.area.dto.command.MatchAreaQuery;
import com.bob.core.domain.area.EmdArea;
import com.bob.core.domain.area.repository.AreaRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("지역 인증 테스트")
@Transactional
@ContainerTest
record AreaAuthenticatorTest(AreaAuthenticator areaAuthenticator, AreaRepository areaRepository) {

    @Test
    void 지역_인증() {
        EmdArea emdArea = createEmdArea();
        areaRepository.save(emdArea);

        MatchAreaQuery query = MatchAreaQuery.of(CENTER_LAT, CENTER_LON);

        assertDoesNotThrow(() -> areaAuthenticator.authenticate(emdArea.getId(), query));
    }

    @Test
    void 지역_인증_시_범위를_벗어나면_사용자_예외가_발생한다() {
        EmdArea emdArea = createEmdArea();
        areaRepository.save(emdArea);

        double lat = CENTER_LAT + 0.02;
        double lon = CENTER_LON + 0.02;
        MatchAreaQuery query = MatchAreaQuery.of(lat, lon);

        assertThatThrownBy(() -> areaAuthenticator.authenticate(emdArea.getId(), query))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(AREA_AUTHENTICATION_FAILED.getMessage());
    }
}
