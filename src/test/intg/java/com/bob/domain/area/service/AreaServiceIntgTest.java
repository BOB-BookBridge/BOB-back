package com.bob.domain.area.service;

import static com.bob.global.exception.response.ApplicationError.INVALID_AREA_AUTHENTICATION;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.OTHER_LAT;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.OTHER_LON;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.customAuthenticationCommand;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.defaultMismatchAuthenticationCommand;
import static com.bob.support.fixture.command.AuthenticationCommandFixture.guestCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bob.domain.area.entity.EmdArea;
import com.bob.domain.area.entity.activity.ActivityArea;
import com.bob.domain.area.repository.ActivityAreaRepository;
import com.bob.domain.area.service.dto.command.AuthenticationCommand;
import com.bob.domain.area.service.reader.ActivityAreaReader;
import com.bob.domain.area.service.reader.EmdAreaReader;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.TestContainerSupport;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("위치 서비스 통합 테스트")
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

  private static int applyAsInt(int b) {
    return b;
  }

  @BeforeEach
  void setUp() {
    defaultEmdArea = emdAreaReader.readEmdAreaById(213); // 역삼동
    otherEmdArea = emdAreaReader.readEmdAreaById(785); // 신곡동
  }

  @Test
  void 위치_인증_및_활동_지역_등록() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    AuthenticationCommand command = customAuthenticationCommand(memberId, otherEmdArea.getId(), OTHER_LAT, OTHER_LON);

    // when
    areaService.authenticateProcess(command);

    // then
    ActivityArea changedArea = activityAreaReader.readActivityAreaByMemberId(memberId);
    assertThat(changedArea.getId().getMemberId()).isEqualTo(command.memberId());
    assertThat(changedArea.getId().getEmdAreaId()).isEqualTo(command.emdId());
  }

  @Test
  void 위치_인증_시_인증_요청_구역과_실제_위경도_위치가_다르면_예외가_발생한다() {
    // given
    AuthenticationCommand command = defaultMismatchAuthenticationCommand();

    // when & then
    assertThatThrownBy(() -> areaService.authenticateProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(INVALID_AREA_AUTHENTICATION.getMessage());
  }

  @Test
  void 익명의_사용자가_위치_인증_시_활동_지역_등록은_생략된다() {
    // given
    AuthenticationCommand command = guestCommand();
    int before = countAll();

    // when
    areaService.authenticateProcess(command);

    // then
    assertThat(countAll()).isEqualTo(before);
  }

  private int countAll() {
    int n = 0;
    for (ActivityArea ignored : activityAreaRepository.findAll())
      n++;
    return n;
  }
}
