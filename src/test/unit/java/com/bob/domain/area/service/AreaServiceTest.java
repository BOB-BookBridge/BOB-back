package com.bob.domain.area.service;

import static com.bob.global.exception.response.ApplicationError.INVALID_AREA_AUTHENTICATION;
import static com.bob.global.exception.response.ApplicationError.IS_NOT_SAME_AREA;
import static com.bob.global.exception.response.ApplicationError.IS_SAME_REQUEST;
import static com.bob.global.exception.response.ApplicationError.NOT_EXISTS_MEMBER;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.defaultAuthenticationCommand;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.defaultChangeAreaCommand;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.defaultReAuthenticateCommand;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.guestCommand;
import static com.bob.support.fixture.domain.EmdAreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.bob.domain.area.entity.EmdArea;
import com.bob.domain.area.entity.SiggArea;
import com.bob.domain.area.entity.activity.ActivityArea;
import com.bob.domain.area.entity.activity.ActivityAreaId;
import com.bob.domain.area.repository.ActivityAreaRepository;
import com.bob.domain.area.service.dto.command.AuthenticationCommand;
import com.bob.domain.area.service.dto.command.AuthenticationPurpose;
import com.bob.domain.area.service.dto.query.ReadAreaQuery;
import com.bob.domain.area.service.dto.response.AreaSummaryResponse;
import com.bob.domain.area.service.reader.ActivityAreaReader;
import com.bob.domain.area.service.reader.EmdAreaReader;
import com.bob.global.exception.exceptions.ApplicationException;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("위치 인증 서비스 테스트")
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
  void 요청한_위치에_사용자의_위치가_포함되는_경우_좌표_인증에_성공한다() {
    AuthenticationCommand command = defaultAuthenticationCommand();
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(true);

    areaService.authenticateProcess(command);
  }

  @Test
  @DisplayName("회원가입 시 행정구역 밖에 있는 경우 - 실패 테스트")
  void 요청한_위치에_사용자의_위치가_포함되지_않는_경우_예외가_발생한다() {
    AuthenticationCommand command = defaultAuthenticationCommand();
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(false);

    assertThatThrownBy(() -> areaService.authenticateProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(INVALID_AREA_AUTHENTICATION.getMessage());
  }

  @Test
  @DisplayName("활동지역 변경 - 성공 테스트")
  void 요청이_유효하면_활동지역_변경에_성공한다() {
    AuthenticationCommand command = defaultChangeAreaCommand();
    ActivityAreaId currentAreaId = new ActivityAreaId(command.memberId(), 999);
    ActivityArea area = mock(ActivityArea.class);
    given(area.getId()).willReturn(currentAreaId);
    given(activityAreaReader.readActivityAreaByMemberId(command.memberId())).willReturn(area);
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(true);

    areaService.authenticateProcess(command);

    then(activityAreaRepository).should().delete(area);
    then(activityAreaRepository).should().save(any(ActivityArea.class));
  }

  @Test
  @DisplayName("활동지역 변경 시 같은 지역일 경우 예외 발생")
  void 활동지역_변경_시_같은_지역에_대해_인증하면_예외가_발생한다() {
    AuthenticationCommand command = defaultChangeAreaCommand();
    ActivityAreaId currentAreaId = new ActivityAreaId(command.memberId(), command.emdId());
    ActivityArea area = mock(ActivityArea.class);
    given(area.getId()).willReturn(currentAreaId);
    given(activityAreaReader.readActivityAreaByMemberId(command.memberId())).willReturn(area);
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(true);

    assertThatThrownBy(() -> areaService.authenticateProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(IS_SAME_REQUEST.getMessage());
  }

  @Test
  @DisplayName("활동지역 변경 시 회원이 아닌 경우 - 실패 테스트")
  void 활동지역_변경_시_로그인을_하지않은_경우_예외가_발생한다() {
    AuthenticationCommand command = guestCommand(AuthenticationPurpose.CHANGE_AREA);
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(true);

    assertThatThrownBy(() -> areaService.authenticateProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(NOT_EXISTS_MEMBER.getMessage());
  }

  @Test
  @DisplayName("활동지역 재인증 - 성공 테스트")
  void 요청이_유효하면_활동지역_재인증에_성공한다() {
    AuthenticationCommand command = defaultReAuthenticateCommand();
    ActivityAreaId currentAreaId = new ActivityAreaId(command.memberId(), 213);
    ActivityArea area = mock(ActivityArea.class);
    given(area.getId()).willReturn(currentAreaId);
    given(activityAreaReader.readActivityAreaByMemberId(command.memberId())).willReturn(area);
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(true);

    areaService.authenticateProcess(command);

    then(area).should().updateAuthenticationAt(any(LocalDate.class));
  }

  @Test
  @DisplayName("활동지역 재인증 시 로그인하지 않은 사용자 - 실패 테스트")
  void 활동지역_재인증_시_로그인한_사용자가_아닌_경우_예외가_발생한다() {
    AuthenticationCommand command = guestCommand(AuthenticationPurpose.RE_AUTHENTICATE);
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(true);

    assertThatThrownBy(() -> areaService.authenticateProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(NOT_EXISTS_MEMBER.getMessage());
  }

  @Test
  @DisplayName("활동지역_재인증_다른_지역_예외")
  void 활동지역_재인증_시_다른_지역일_경우_예외가_발생한다() {
    AuthenticationCommand command = defaultReAuthenticateCommand();
    ActivityAreaId currentAreaId = new ActivityAreaId(command.memberId(), 999);
    ActivityArea area = mock(ActivityArea.class);
    given(area.getId()).willReturn(currentAreaId);
    given(activityAreaReader.readActivityAreaByMemberId(command.memberId())).willReturn(area);
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(true);

    assertThatThrownBy(() -> areaService.authenticateProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(IS_NOT_SAME_AREA.getMessage());

    then(area).should(never()).updateAuthenticationAt(any());
  }

  @Test
  @DisplayName("활동지역 요약정보 조회 - 성공 테스트")
  void 활동지역_요약정보를_조회할_수_있다() {
    // given
    UUID memberId = MEMBER_ID;
    ReadAreaQuery query = ReadAreaQuery.of(memberId);
    ActivityArea activityArea = mock(ActivityArea.class);
    EmdArea emdArea = mock(EmdArea.class);
    SiggArea siggArea = mock(SiggArea.class);

    given(activityAreaReader.readActivityAreaByMemberId(memberId)).willReturn(activityArea);
    given(activityArea.getId()).willReturn(new ActivityAreaId(memberId, EMD_AREA_ID));
    given(activityArea.getAuthenticationAt()).willReturn(LocalDate.now());
    given(activityArea.isValidAuthentication()).willReturn(true);
    given(emdAreaReader.readEmdAreaById(EMD_AREA_ID)).willReturn(emdArea);
    given(emdArea.getName()).willReturn("역삼동");
    given(emdArea.getSiggArea()).willReturn(siggArea);
    given(siggArea.getName()).willReturn("강남구");

    // when
    AreaSummaryResponse response = areaService.readAreaSummaryProcess(query);

    // then
    assertThat(response.emdId()).isEqualTo(EMD_AREA_ID);
    assertThat(response.emdName()).isEqualTo("역삼동");
    assertThat(response.siggName()).isEqualTo("강남구");
    assertThat(response.validity()).isTrue();
    assertThat(response.authenticatedAt()).isEqualTo(LocalDate.now());
  }
}
