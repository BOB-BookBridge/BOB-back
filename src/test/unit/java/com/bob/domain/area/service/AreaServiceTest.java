package com.bob.domain.area.service;

import static com.bob.global.exception.response.ApplicationError.INVALID_AREA_AUTHENTICATION;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.defaultAuthenticationCommand;
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
  void 위치_인증_및_활동_지역_등록() {
    // given
    AuthenticationCommand command = defaultAuthenticationCommand();
    ActivityArea area = mock(ActivityArea.class);
    given(activityAreaReader.readActivityAreaByMemberId(command.memberId())).willReturn(area);
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(true);

    // when
    areaService.authenticateProcess(command);

    // then
    then(activityAreaRepository).should().delete(area);
    then(activityAreaRepository).should().save(any(ActivityArea.class));
  }

  @Test
  void 위치_인증_시_인증_요청_구역과_실제_위경도_위치가_다르면_예외가_발생한다() {
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
  void 회원가입_목적의_위치_인증_시_활동_지역_등록은_생략된다() {
    // given
    AuthenticationCommand command = guestCommand();
    given(emdAreaReader.readEmdAreaById(command.emdId())).willReturn(emdArea);
    given(emdArea.getGeom()).willReturn(geometry);
    given(geometry.contains(any(Point.class))).willReturn(true);

    // when
    areaService.authenticateProcess(command);

    // then
    then(activityAreaRepository).should(never()).save(any(ActivityArea.class));
    then(activityAreaRepository).should(never()).findByIdMemberId(any(UUID.class));
  }

  @Test
  void 활동_지역_정보_조회() {
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
