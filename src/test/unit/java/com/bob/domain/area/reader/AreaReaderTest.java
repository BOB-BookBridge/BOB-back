package com.bob.domain.area.reader;

import static com.bob.support.fixture.domain.EmdAreaFixture.defaultEmdArea;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.bob.domain.area.entity.EmdArea;
import com.bob.domain.area.repository.EmdAreaRepository;
import com.bob.domain.area.service.reader.EmdAreaReader;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("읍/면/동 조회 테스트")
@ExtendWith(MockitoExtension.class)
class AreaReaderTest {

  @Mock
  private EmdAreaRepository areaRepository;

  @InjectMocks
  private EmdAreaReader areaReader;

  @Test
  @DisplayName("EmdArea 조회 - 성공 테스트")
  void emdId로_정상적으로_조회된다() {
    // given
    EmdArea emdArea = defaultEmdArea();
    given(areaRepository.findById(emdArea.getId())).willReturn(Optional.of(emdArea));

    // when
    EmdArea result = areaReader.readEmdAreaById(emdArea.getId());

    // then
    assertThat(result).isEqualTo(emdArea);
  }

  @Test
  @DisplayName("EmdArea 조회 - 실패 테스트 (존재 X)")
  void 존재하지_않는_emdId인_경우_예외가_발생한다() {
    // given
    given(areaRepository.findById(any())).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> areaReader.readEmdAreaById(-1))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_EXISTS_AREA.getMessage());
  }
}
