package com.bob.domain.area.service;

import static com.bob.domain.area.service.dto.command.AuthenticationPurpose.CHANGE_AREA;
import static com.bob.domain.area.service.dto.command.AuthenticationPurpose.RE_AUTHENTICATE;
import static com.bob.global.exception.response.ApplicationError.INVALID_AREA_AUTHENTICATION;
import static com.bob.global.exception.response.ApplicationError.NOT_EXISTS_MEMBER;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.DEFAULT_LAT;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.DEFAULT_LON;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.OTHER_LAT;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.OTHER_LON;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.customAuthenticationCommand;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.defaultAuthenticationCommand;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.defaultMismatchAuthenticationCommand;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.guestCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bob.domain.area.entity.EmdArea;
import com.bob.domain.area.entity.activity.ActivityArea;
import com.bob.domain.area.repository.ActivityAreaRepository;
import com.bob.domain.area.service.dto.command.AuthenticationCommand;
import com.bob.domain.area.service.dto.command.AuthenticationPurpose;
import com.bob.domain.area.service.reader.ActivityAreaReader;
import com.bob.domain.area.service.reader.EmdAreaReader;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.TestContainerSupport;
import com.bob.support.redis.RedisContainerConfig;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("위치 서비스 통합 테스트")
@Import(RedisContainerConfig.class)
@Transactional
@SpringBootTest
class AreaServiceIntgTest extends TestContainerSupport {

  @Autowired
  private AreaService areaService;

  @Autowired
  private ActivityAreaRepository activityAreaRepository;

  @Autowired
  private ActivityAreaReader activityAreaReader;

  @Autowired
  private EmdAreaReader emdAreaReader;

  private EmdArea defaultEmdArea;

  private EmdArea otherEmdArea;

  private static Stream<AuthenticationPurpose> 요청_목록() {
    return Stream.of(CHANGE_AREA, RE_AUTHENTICATE);
  }

  @BeforeEach
  void setUp() {
    defaultEmdArea = emdAreaReader.readEmdAreaById(213); // 역삼동
    otherEmdArea = emdAreaReader.readEmdAreaById(785); // 신곡동
  }

  @Test
  @DisplayName("사용자 위치가 활동지역과 일치 - 성공 테스트")
  void 회원가입_시_위치인증_성공() {
    // given
    AuthenticationCommand command = defaultAuthenticationCommand();

    // when & then
    areaService.authenticateProcess(command);
  }

  @Test
  @DisplayName("사용자 위치가 활동지역과 불일치 - 실패 테스트")
  void 회원가입_시_위치인증_실패() {
    // given
    AuthenticationCommand command = defaultMismatchAuthenticationCommand();

    // when & then
    assertThatThrownBy(() -> areaService.authenticateProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(INVALID_AREA_AUTHENTICATION.getMessage());
  }

  @Test
  @DisplayName("활동지역 변경 - 성공 테스트")
  void 활동지역_변경() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    AuthenticationCommand command = customAuthenticationCommand(
        memberId,
        otherEmdArea.getId(),
        OTHER_LAT, OTHER_LON,
        CHANGE_AREA
    );

    // when
    areaService.authenticateProcess(command);

    // then
    ActivityArea changedArea = activityAreaReader.readActivityAreaByMemberId(memberId);
    assertThat(changedArea.getId().getMemberId()).isEqualTo(command.memberId());
    assertThat(changedArea.getId().getEmdAreaId()).isEqualTo(command.emdId());
  }

  @Test
  @DisplayName("활동지역 재인증 - 성공 테스트")
  void 재인증_성공() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    ActivityArea area = activityAreaReader.readActivityAreaByMemberId(memberId);
    AuthenticationCommand command = customAuthenticationCommand(
        memberId,
        defaultEmdArea.getId(),
        DEFAULT_LAT, DEFAULT_LON,
        RE_AUTHENTICATE
    );

    // when
    areaService.authenticateProcess(command);

    // then
    assertThat(area.getId().getMemberId()).isEqualTo(memberId);
    assertThat(area.getId().getEmdAreaId()).isEqualTo(defaultEmdArea.getId());
    assertThat(area.getAuthenticationAt()).isEqualTo(LocalDate.now());
  }

  @ParameterizedTest
  @MethodSource("요청_목록") // CHANGE_AREA: 위치 변경, RE_AUTHENTICATE: 위치 재인증
  @DisplayName("비회원 인증 요청 - 실패 테스트")
  void 게스트_예외(AuthenticationPurpose purpose) {
    // given
    AuthenticationCommand command = guestCommand(purpose);

    // when & then
    assertThatThrownBy(() -> areaService.authenticateProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(NOT_EXISTS_MEMBER.getMessage());
  }
}
