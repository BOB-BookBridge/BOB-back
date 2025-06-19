package com.bob.domain.area.service;

import static com.bob.global.exception.response.ApplicationError.INVALID_AREA_AUTHENTICATION;
import static com.bob.global.exception.response.ApplicationError.IS_NOT_SAME_AREA;
import static com.bob.global.exception.response.ApplicationError.NOT_EXISTS_MEMBER;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.defaultAuthenticationCommand;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.defaultChangeAreaCommand;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.defaultReAuthenticateCommand;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.guestCommand;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.bob.domain.area.entity.EmdArea;
import com.bob.domain.area.entity.activity.ActivityArea;
import com.bob.domain.area.entity.activity.ActivityAreaId;
import com.bob.domain.area.repository.ActivityAreaRepository;
import com.bob.domain.area.service.dto.command.AuthenticationCommand;
import com.bob.domain.area.service.dto.command.AuthenticationPurpose;
import com.bob.domain.area.service.reader.ActivityAreaReader;
import com.bob.domain.area.service.reader.EmdAreaReader;
import com.bob.global.exception.exceptions.ApplicationException;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("위치 인증 서비스 단위 테스트")
@ExtendWith(MockitoExtension.class)
class AreaServiceTest {

  @InjectMocks
  private AreaService areaService;

  @Mock
  private ActivityAreaReader activityAreaReader;

  @Mock
  private ActivityAreaRepository activityAreaRepository;

  @Mock
  private EmdAreaReader emdAreaReader;

  @Mock
  private EmdArea emdArea;

  @Mock
  private Geometry geometry;

  @Test
  @DisplayName("회원가입 시 행정구역 안에 있는 경우 - 성공 테스트")
  void 회원가입_좌표_인증_성공() {
    // given
    AuthenticationCommand command = defaultAuthenticationCommand();
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(true);

    // when & then
    areaService.authenticateProcess(command);
  }

  @Test
  @DisplayName("회원가입 시 행정구역 밖에 있는 경우 - 실패 테스트")
  void 행정구역_밖이면_예외_발생() {
    // given
    AuthenticationCommand command = defaultAuthenticationCommand();
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(false);

    // when & then
    assertThatThrownBy(() -> areaService.authenticateProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(INVALID_AREA_AUTHENTICATION.getMessage());
  }

  @Test
  @DisplayName("활동지역 변경 - 성공 테스트")
  void 활동지역_변경_성공() {
    // given
    AuthenticationCommand command = defaultChangeAreaCommand();
    ActivityAreaId currentAreaId = new ActivityAreaId(command.memberId(), 999);
    ActivityArea area = mock(ActivityArea.class);
    given(area.getId()).willReturn(currentAreaId);
    given(activityAreaReader.readActivityAreaByMemberId(command.memberId())).willReturn(area);
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(true);

    // when
    areaService.authenticateProcess(command);

    // then
    then(activityAreaRepository).should(times(1)).delete(any(ActivityArea.class));
    then(activityAreaRepository).should(times(1)).save(any(ActivityArea.class));
  }

  @Test
  @DisplayName("활동지역 변경 시 회원이 아닌 경우 - 실패 테스트")
  void 활동지역_변경_게스트_예외() {
    // given
    AuthenticationCommand command = guestCommand(AuthenticationPurpose.CHANGE_AREA);
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(true);

    // when & then
    assertThatThrownBy(() -> areaService.authenticateProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(NOT_EXISTS_MEMBER.getMessage());
  }

  @Test
  @DisplayName("활동지역 재인증 - 성공 테스트")
  void 활동지역_재인증_성공() {
    // given
    AuthenticationCommand command = defaultReAuthenticateCommand(); // emdId = 213
    ActivityAreaId currentAreaId = new ActivityAreaId(command.memberId(), 213);
    ActivityArea area = mock(ActivityArea.class);
    given(area.getId()).willReturn(currentAreaId);
    given(activityAreaReader.readActivityAreaByMemberId(command.memberId())).willReturn(area);
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(true);

    // when
    areaService.authenticateProcess(command);

    // then
    then(activityAreaReader).should().readActivityAreaByMemberId(command.memberId());
    then(area).should().updateAuthenticationAt(any(LocalDate.class));
  }

  @Test
  @DisplayName("활동지역 재인증 시 회원이 아닌 경우 - 실패 테스트")
  void 활동지역_재인증_시_로그인한_사용자가_아니면_예외가_발생한다() {
    // given
    AuthenticationCommand command = guestCommand(AuthenticationPurpose.RE_AUTHENTICATE);
    EmdArea emdArea = mock(EmdArea.class);
    Geometry geometry = mock(Geometry.class);
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(true);

    // when & then
    assertThatThrownBy(() -> areaService.authenticateProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(NOT_EXISTS_MEMBER.getMessage());
  }

  @Test
  @DisplayName("활동지역 재인증 시 동일한 지역이 아닌 경우 - 실패 테스트")
  void 활동지역_재인증_시_다른_지역인_경우_예외가_발생한다() {
    // given
    AuthenticationCommand command = defaultReAuthenticateCommand(); // emdId = 213
    ActivityAreaId currentAreaId = new ActivityAreaId(command.memberId(), 999);
    ActivityArea mockArea = mock(ActivityArea.class);
    given(mockArea.getId()).willReturn(currentAreaId);
    given(activityAreaReader.readActivityAreaByMemberId(command.memberId())).willReturn(mockArea);
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(true);

    // when & then
    assertThatThrownBy(() -> areaService.authenticateProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(IS_NOT_SAME_AREA.getMessage());

    then(mockArea).should(never()).updateAuthenticationAt(any());
  }
}
