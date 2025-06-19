package com.bob.domain.member.service;

import static com.bob.support.fixture.command.ChangeProfileCommandFixture.defaultChangeProfileCommand;
import static com.bob.support.fixture.command.ChangeProfileCommandFixture.sameNicknameChangeProfileCommand;
import static com.bob.support.fixture.command.ChangeProfileImageUrlCommandFixture.customChangeProfileImageUrlCommand;
import static com.bob.support.fixture.command.MemberCommandFixture.customChangePasswordCommand;
import static com.bob.support.fixture.command.MemberCommandFixture.defaultCreateMemberCommand;
import static com.bob.support.fixture.command.MemberCommandFixture.defaultIssuePasswordCommand;
import static com.bob.support.fixture.domain.MemberFixture.customEmailMember;
import static com.bob.support.fixture.domain.MemberFixture.defaultMember;
import static com.bob.support.fixture.domain.MemberFixture.encryptPasswordMember;
import static com.bob.support.fixture.response.MemberAreaSummaryResponseFixture.DEFAULT_AREA_SUMMARY_RESPONSE;
import static com.bob.support.fixture.response.MemberProfileImageUrlResponseFixture.DEFAULT_MEMBER_PROFILE_IMAGE_URL_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

import com.bob.domain.member.entity.Member;
import com.bob.domain.member.repository.MemberRepository;
import com.bob.domain.member.service.dto.command.ChangePasswordCommand;
import com.bob.domain.member.service.dto.command.ChangeProfileCommand;
import com.bob.domain.member.service.dto.command.ChangeProfileImageUrlCommand;
import com.bob.domain.member.service.dto.command.CreateMemberCommand;
import com.bob.domain.member.service.dto.command.IssuePasswordCommand;
import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberProfileImageUrlResponse;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.service.port.out.MemberAreaPort;
import com.bob.domain.member.service.port.out.MemberMailPort;
import com.bob.domain.member.service.port.out.MemberProfileImageAccessor;
import com.bob.domain.member.service.port.out.MemberRedisPort;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.global.utils.random.RandomUtils;
import com.bob.support.TestContainerSupport;
import com.bob.support.redis.RedisContainerConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("사용자 서비스 통합 테스트")
@Import(RedisContainerConfig.class)
@Transactional
@SpringBootTest
class MemberServiceIntgTest extends TestContainerSupport {

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberRepository memberRepository;

  @MockitoBean
  private MemberAreaPort areaPort;

  @MockitoBean
  private MemberRedisPort redisPort;

  @MockitoBean
  private MemberProfileImageAccessor profileImageAccessor;

  @MockitoBean
  private MemberMailPort mailPort;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  @DisplayName("회원가입 - 성공 테스트")
  void 이메일_인증이_완료된_사용자는_회원가입을_할_수_있다() {
    // given
    String email = "test@email.com";
    CreateMemberCommand command = defaultCreateMemberCommand();
    given(redisPort.isVerified(email)).willReturn(true);

    // when
    memberService.signupProcess(command);

    // then
    Member member = memberRepository.findByEmail(email).orElseThrow();
    assertThat(member.getEmail()).isEqualTo(email);
    assertThat(passwordEncoder.matches(command.password(), member.getPassword())).isTrue();
    assertThat(member.getNickname()).isEqualTo(command.nickname());
  }

  @Test
  @DisplayName("회원가입 - 실패 테스트(이메일 인증 X)")
  void 이메일_인증을_하지않은_사용자는_회원가입을_할_수_없다() {
    // given
    CreateMemberCommand command = defaultCreateMemberCommand();

    // when & then
    assertThatThrownBy(() -> memberService.signupProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.UNVERIFIED_EMAIL.getMessage());
  }

  @Test
  @DisplayName("회원가입 - 실패 테스트(중복된 이메일)")
  void 동일한_이메일_계정이_존재하면_사용자는_회원가입을_할_수_없다() {
    // given
    String duplicateEmail = "test@email.com";
    given(redisPort.isVerified(duplicateEmail)).willReturn(true);

    Member existing = customEmailMember(duplicateEmail);
    memberRepository.save(existing);
    CreateMemberCommand command = defaultCreateMemberCommand();

    // when & then
    assertThatThrownBy(() -> memberService.signupProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.ALREADY_EXISTS_EMAIL.getMessage());
  }

  @Test
  @DisplayName("사용자 프로필 조회 - 성공 테스트")
  void 회원은_자신의_프로필을_조회할_수_있다() {
    // given
    Member member = defaultMember();
    memberRepository.save(member);
    ReadProfileQuery query = ReadProfileQuery.of(member.getId());
    given(areaPort.readMemberAreaSummary(member.getId())).willReturn(DEFAULT_AREA_SUMMARY_RESPONSE);

    // when
    MemberProfileResponse response = memberService.readProfileProcess(query);

    // then
    assertThat(response.memberId()).isEqualTo(member.getId());
    assertThat(response.nickname()).isEqualTo(member.getNickname());
    assertThat(response.profileImageUrl()).isEqualTo(member.getProfileImageUrl());
    assertThat(response.area()).isNotNull();
  }

  @Test
  @DisplayName("프로필 변경 - 성공 테스트")
  void 닉네임이_다르면_프로필을_변경할_수_있다() {
    // given
    Member member = defaultMember();
    memberRepository.save(member);
    ChangeProfileCommand command = defaultChangeProfileCommand(member.getId());

    // when
    memberService.changeProfileProcess(command);

    // then
    assertThat(member.getNickname()).isEqualTo(command.nickname());
  }

  @Test
  @DisplayName("프로필 변경 - 실패 테스트(동일한 닉네임)")
  void 닉네임이_동일하면_프로필_변경에_실패한다() {
    // given
    Member member = defaultMember();
    memberRepository.save(member);

    ChangeProfileCommand command = sameNicknameChangeProfileCommand(member.getId());

    // when & then
    assertThatThrownBy(() -> memberService.changeProfileProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.IS_SAME_REQUEST.getMessage());
  }

  @Test
  @DisplayName("임시 비밀번호 발급 - 성공 테스트")
  void 이메일로_임시_비밀번호를_받아_비밀번호를_변경할_수_있다() {
    // given
    Member member = defaultMember();
    memberRepository.save(member);
    IssuePasswordCommand command = defaultIssuePasswordCommand();

    // when
    memberService.issueTempPasswordProcess(command);

    // then
    assertThat(member.getPassword()).isNotEqualTo(defaultMember().getPassword());
  }

  @Test
  @DisplayName("임시 비밀번호 발급 - 실패 테스트(존재하지 않는 이메일)")
  void 존재하지_않는_이메일이면_임시_비밀번호_발급에_실패한다() {
    // given
    String notExistsEmail = "unknown@email.com";
    IssuePasswordCommand command = new IssuePasswordCommand(notExistsEmail);

    // when & then
    assertThatThrownBy(() -> memberService.issueTempPasswordProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_EXISTS_MEMBER.getMessage());
  }

  @Test
  @DisplayName("비밀번호 변경 - 성공 테스트")
  void 기존_비밀번호가_일치하면_비밀번호를_변경할_수_있다() {
    // given
    String password = "password";
    String newPassword = "new-password";
    Member member = encryptPasswordMember(passwordEncoder.encode(password));
    memberRepository.save(member);
    ChangePasswordCommand command = customChangePasswordCommand(member.getId(), password, newPassword);

    // when
    memberService.changePasswordProcess(command);

    // then
    assertThat(passwordEncoder.matches(newPassword, member.getPassword())).isTrue();
  }

  @Test
  @DisplayName("비밀번호 변경 - 실패 테스트(기존 비밀번호 불일치)")
  void 기존_비밀번호가_일치하지_않으면_비밀번호를_변경할_수_없다() {
    // given
    String password = "password";
    String wrongOldPassword = "wrong-password";
    String newPassword = "new-password";

    Member member = encryptPasswordMember(passwordEncoder.encode(password));
    memberRepository.save(member);

    ChangePasswordCommand command = customChangePasswordCommand(member.getId(), wrongOldPassword, newPassword);

    // when & then
    assertThatThrownBy(() -> memberService.changePasswordProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.INVALID_OLD_PASSWORD.getMessage());
  }

  @Test
  @DisplayName("프로필 이미지 Presigned URL 발급 및 경로 저장 - 통합 성공 테스트")
  void 프로필_이미지_업로드를_위한_URL을_발급하고_DB에_경로를_저장할_수_있다() {
    // given
    Member member = defaultMember();
    memberRepository.save(member);
    ChangeProfileImageUrlCommand command = customChangeProfileImageUrlCommand(member.getId());
    MemberProfileImageUrlResponse expected = DEFAULT_MEMBER_PROFILE_IMAGE_URL_RESPONSE;
    given(profileImageAccessor.getImageUploadUrl(anyString(), eq(command.contentType()))).willReturn(expected.imageUploadUrl());

    // when
    MemberProfileImageUrlResponse response = memberService.changeProfileImageUrlProcess(command);

    // then
    assertThat(member.getProfileImageUrl()).isEqualTo(response.fileName());
    assertThat(response.imageUploadUrl()).isEqualTo(expected.imageUploadUrl());
    assertThat(response.fileName()).startsWith("profile");
    assertThat(response.fileName()).endsWith(".png");
  }
}
