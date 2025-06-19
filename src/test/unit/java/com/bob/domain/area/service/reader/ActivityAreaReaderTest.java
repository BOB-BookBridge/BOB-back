package com.bob.domain.area.service.reader;

import static com.bob.support.fixture.domain.ActivityAreaFixture.defaultActivityArea;
import static com.bob.support.fixture.domain.ActivityAreaFixture.defaultActivityAreaId;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.bob.domain.area.entity.activity.ActivityArea;
import com.bob.domain.area.entity.activity.ActivityAreaId;
import com.bob.domain.area.repository.ActivityAreaRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("활동지역 조회 테스트")
@ExtendWith(MockitoExtension.class)
class ActivityAreaReaderTest {

  @InjectMocks
  private ActivityAreaReader reader;

  @Mock
  private ActivityAreaRepository activityAreaRepository;

  @Test
  @DisplayName("활동지역 조회 - 성공")
  void 활동지역을_정상적으로_조회할_수_있다() {
    // given
    ActivityArea expected = defaultActivityArea();
    given(activityAreaRepository.findByIdMemberId(MEMBER_ID)).willReturn(Optional.of(expected));

    // when
    ActivityArea result = reader.readActivityAreaByMemberId(expected.getId().getMemberId());

    // then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  @DisplayName("활동지역 조회 - 실패(존재하지 않음)")
  void 활동지역이_존재하지_않으면_예외가_발생한다() {
    // given
    ActivityAreaId id = defaultActivityAreaId();
    given(activityAreaRepository.findByIdMemberId(MEMBER_ID)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> reader.readActivityAreaByMemberId(id.getMemberId()))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(ApplicationError.NOT_EXISTS_ACTIVITY_AREA.getMessage());
  }
}