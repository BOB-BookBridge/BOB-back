package com.bob.domain.member.service;

import static com.bob.domain.member.entity.Status.ACTIVE;
import static com.bob.domain.member.entity.Status.WITHDRAW;
import static com.bob.global.exception.response.ApplicationError.ALREADY_EXISTS_EMAIL;
import static com.bob.global.exception.response.ApplicationError.INVALID_OLD_PASSWORD;
import static com.bob.global.exception.response.ApplicationError.IS_SAME_REQUEST;
import static com.bob.global.exception.response.ApplicationError.NOT_EXISTS_MEMBER;
import static com.bob.global.exception.response.ApplicationError.UNVERIFIED_EMAIL;
import static com.bob.support.fixture.command.ChangeProfileCommandFixture.sameChangeProfileCommand;
import static com.bob.support.fixture.command.MemberCommandFixture.defaultChangePasswordCommand;
import static com.bob.support.fixture.command.MemberCommandFixture.defaultCreateMemberCommand;
import static com.bob.support.fixture.command.MemberCommandFixture.defaultIssuePasswordCommand;
import static com.bob.support.fixture.domain.EmdAreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.defaultIdMember;
import static com.bob.support.fixture.domain.MemberFixture.defaultMember;
import static com.bob.support.fixture.domain.MemberFixture.removedMember;
import static com.bob.support.fixture.query.MemberQueryFixture.READ_ME_PROFILE_QUERY;
import static com.bob.support.fixture.query.MemberQueryFixture.READ_OTHER_PROFILE_QUERY;
import static com.bob.support.fixture.response.MemberAreaSummaryResponseFixture.DEFAULT_AREA_SUMMARY_RESPONSE;
import static com.bob.support.fixture.response.MemberBooksResponseFixture.DEFAULT_MEMBER_BOOKS_RESPONSE;
import static com.bob.support.fixture.response.interest.InterestNamesFixture.DEFAULT_INTEREST_DISPLAY_NAMES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.bob.domain.member.entity.Member;
import com.bob.domain.member.repository.MemberRepository;
import com.bob.domain.member.service.dto.command.ChangePasswordCommand;
import com.bob.domain.member.service.dto.command.ChangeProfileCommand;
import com.bob.domain.member.service.dto.command.CreateMemberCommand;
import com.bob.domain.member.service.dto.command.IssuePasswordCommand;
import com.bob.domain.member.service.dto.command.RecoverAccountCommand;
import com.bob.domain.member.service.dto.command.RemoveMemberCommand;
import com.bob.domain.member.service.dto.command.SocialLoginCommand;
import com.bob.domain.member.service.dto.query.ReadMemberBooksQuery;
import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.service.dto.response.SocialLoginResponse;
import com.bob.domain.member.service.port.out.MemberAreaPort;
import com.bob.domain.member.service.port.out.MemberMailPort;
import com.bob.domain.member.service.port.out.MemberRedisPort;
import com.bob.domain.member.service.reader.MemberReader;
import com.bob.global.event.application.dto.member.AccountEvent;
import com.bob.global.exception.exceptions.ApplicationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@DisplayName("사용자 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @InjectMocks
  private MemberService memberService;

  @Mock
  private MemberInterestService memberInterestService;

  @Mock
  private MemberBookService memberBookService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private MemberReader memberReader;

  @Mock
  private MemberMailPort mailPort;

  @Mock
  private MemberRedisPort redisPort;

  @Mock
  private MemberAreaPort areaPort;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Mock
  private BCryptPasswordEncoder encoder;

  @Test
  @DisplayName("회원가입 - 성공 테스트")
  void 회원가입을_진행할_수_있다() {
    // given
    CreateMemberCommand command = defaultCreateMemberCommand();
    String encodedPassword = "$2a$10";
    given(encoder.encode(command.password())).willReturn(encodedPassword);
    given(redisPort.isVerified(command.email())).willReturn(true);
    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

    // when
    memberService.signupProcess(command);

    // then
    then(redisPort).should().isVerified(command.email());
    then(redisPort).should().deleteVerified(command.email());
    then(memberRepository).should(times(1)).save(captor.capture());
    Member member = captor.getValue();
    then(areaPort).should().createMemberActivityArea(member.getId(), command.emdId());
    assertThat(member.getEmail()).isEqualTo(command.email());
    assertThat(member.getNickname()).isEqualTo(command.nickname());
    assertThat(member.getPassword()).isEqualTo(encodedPassword);
  }

  @Test
  @DisplayName("회원가입 - 실패 테스트(이메일 인증 X)")
  void 이메일_인증이_되지_않으면_회원가입에_실패한다() {
    // given
    CreateMemberCommand command = defaultCreateMemberCommand();
    given(redisPort.isVerified(command.email())).willReturn(false);

    // when & then
    assertThatThrownBy(() -> memberService.signupProcess(command))
        .isInstanceOf(RuntimeException.class)
        .hasMessage(UNVERIFIED_EMAIL.getMessage());
  }

  @Test
  @DisplayName("회원가입 - 실패 테스트(이미 존재하는 이메일 계정)")
  void 이메일_계정이_존재하면_회원가입에_실패한다() {
    // given
    CreateMemberCommand command = defaultCreateMemberCommand();
    given(memberRepository.existsByEmail(command.email())).willReturn(true);

    // when & then
    assertThatThrownBy(() -> memberService.signupProcess(command))
        .isInstanceOf(RuntimeException.class)
        .hasMessage(ALREADY_EXISTS_EMAIL.getMessage());
  }

  @Test
  void 소셜_로그인_회원가입_및_정보_반환() {
    // given
    SocialLoginCommand command = SocialLoginCommand.of("NAVER", "test@naver.com", "foo");
    given(memberRepository.findByEmail("test@naver.com")).willReturn(Optional.empty());
    given(encoder.encode(anyString())).willReturn("$2a$encodedDummy");

    Member saved = defaultIdMember();
    given(memberRepository.save(any(Member.class))).willReturn(saved);

    // when
    SocialLoginResponse result = memberService.socialLoginProcess(command);

    // then
    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
    then(memberRepository).should(times(1)).save(captor.capture());

    Member toSave = captor.getValue();
    assertThat(toSave.getEmail()).isEqualTo("test@naver.com");
    assertThat(toSave.getNickname()).isEqualTo("foo");
    assertThat(toSave.getPassword()).isEqualTo("$2a$encodedDummy");
    then(areaPort).should(times(1)).createNonAuthenticatedActivityArea(saved.getId(), command.emdId());
    assertThat(result.memberId()).isEqualTo(saved.getId());
    assertThat(result.status()).isEqualTo(saved.getStatus().name());
  }

  @Test
  void 소셜_로그인_정보_반환() {
    // given
    SocialLoginCommand command = SocialLoginCommand.of("GOOGLE", "test@google.com", "foo");
    given(memberRepository.findByEmail("test@google.com")).willReturn(Optional.of(defaultIdMember()));

    // when
    SocialLoginResponse result = memberService.socialLoginProcess(command);

    // then
    assertThat(result.memberId()).isEqualTo(defaultIdMember().getId());
    assertThat(result.status()).isEqualTo(defaultIdMember().getStatus().name());
    then(memberRepository).should(never()).save(any(Member.class));
    then(areaPort).should(never()).createNonAuthenticatedActivityArea(any(), any());
  }

  @Test
  void 내_프로필_조회() {
    // given
    Member member = defaultMember();
    ReadProfileQuery query = READ_ME_PROFILE_QUERY;

    given(memberReader.readMemberById(query.memberId())).willReturn(member);
    given(memberInterestService.readMemberInterests(member.getId())).willReturn(DEFAULT_INTEREST_DISPLAY_NAMES());
    given(areaPort.readMemberAreaSummary(MEMBER_ID)).willReturn(DEFAULT_AREA_SUMMARY_RESPONSE);
    given(memberBookService.readMemberBooksProcess(ReadMemberBooksQuery.of(member.getId()))).willReturn(DEFAULT_MEMBER_BOOKS_RESPONSE);

    // when
    MemberProfileResponse response = memberService.readProfileProcess(query);

    // then
    then(memberReader).should(times(1)).readMemberById(query.memberId());
    assertThat(response.isSocial()).isFalse();
    assertThat(response.memberId()).isEqualTo(member.getId());
    assertThat(response.email()).isEqualTo(member.getEmail());
    assertThat(response.nickname()).isEqualTo(member.getNickname());
    assertThat(response.interests()).hasSize(DEFAULT_INTEREST_DISPLAY_NAMES().size());
    assertThat(response.area().emdId()).isEqualTo(EMD_AREA_ID);
    assertThat(response.area().isAuthentication()).isTrue();

    // 내 프로필 조회 시 소유 도서 목록 포함
    assertThat(response.bookcase()).isNotNull();
    assertThat(response.bookcase()).hasSize(2);
    assertThat(response.bookcase().get(0).id()).isEqualTo(1L);
    assertThat(response.bookcase().get(1).id()).isEqualTo(2L);
  }

  @Test
  void 타인_프로필_조회() {
    // given
    Member member = defaultMember();
    ReadProfileQuery query = READ_OTHER_PROFILE_QUERY;

    given(memberReader.readMemberById(query.memberId())).willReturn(member);
    given(memberInterestService.readMemberInterests(member.getId())).willReturn(DEFAULT_INTEREST_DISPLAY_NAMES());
    given(areaPort.readMemberAreaSummary(MEMBER_ID)).willReturn(DEFAULT_AREA_SUMMARY_RESPONSE);
    given(memberBookService.readMemberBooksProcess(any(ReadMemberBooksQuery.class))).willReturn(DEFAULT_MEMBER_BOOKS_RESPONSE);

    // when
    MemberProfileResponse response = memberService.readProfileProcess(query);

    // then
    then(memberReader).should(times(1)).readMemberById(query.memberId());
    assertThat(response.isSocial()).isFalse();
    assertThat(response.memberId()).isEqualTo(member.getId());
    assertThat(response.email()).isEqualTo(member.getEmail());
    assertThat(response.nickname()).isEqualTo(member.getNickname());
    assertThat(response.interests()).hasSize(DEFAULT_INTEREST_DISPLAY_NAMES().size());
    assertThat(response.area().emdId()).isEqualTo(EMD_AREA_ID);
    assertThat(response.area().isAuthentication()).isTrue();
    assertThat(response.bookcase()).isNotNull();
  }

  @Test
  void 탈퇴한_사용자의_프로필_조회_테스트() {
    // given
    Member member = removedMember();
    ReadProfileQuery query = READ_OTHER_PROFILE_QUERY;

    given(memberReader.readMemberById(query.memberId())).willReturn(member);
    given(memberInterestService.readMemberInterests(member.getId())).willReturn(DEFAULT_INTEREST_DISPLAY_NAMES());
    given(areaPort.readMemberAreaSummary(MEMBER_ID)).willReturn(DEFAULT_AREA_SUMMARY_RESPONSE);
    given(memberBookService.readMemberBooksProcess(any(ReadMemberBooksQuery.class))).willReturn(DEFAULT_MEMBER_BOOKS_RESPONSE);

    // when
    MemberProfileResponse response = memberService.readProfileProcess(query);

    // then
    then(memberReader).should(times(1)).readMemberById(query.memberId());
    assertThat(response.memberId()).isEqualTo(member.getId());
    assertThat(response.email()).isEqualTo("delete");
    assertThat(response.nickname()).isEqualTo("(알 수 없음)");
    assertThat(response.profileImageUrl()).isNull();
    assertThat(response.interests()).hasSize(0);
    assertThat(response.bookcase()).isNotNull();
  }

  @ParameterizedTest(name = "프로필 변경 성공 케이스: {0}")
  @MethodSource("provideChangeProfileSuccessCases")
  void 프로필_변경(String caseName, String newNickname, List<String> newInterests) {
    // given
    Member member = defaultIdMember();
    ChangeProfileCommand command = new ChangeProfileCommand(member.getId(), newNickname, newInterests);

    given(memberReader.readMemberById(member.getId())).willReturn(member);
    given(memberInterestService.readMemberInterests(member.getId())).willReturn(DEFAULT_INTEREST_DISPLAY_NAMES());

    // when
    memberService.changeProfileProcess(command);

    // then
    then(memberReader).should().readMemberById(member.getId());
    then(memberInterestService).should().readMemberInterests(member.getId());
    then(memberInterestService).should().changeMemberInterests(member.getId(), command.interests());
    assertThat(member.getNickname()).isEqualTo(command.nickname());
  }

  @Test
  void 프로필_수정_별명_관심사_동일_요청() {
    // given
    Member member = defaultIdMember();
    ChangeProfileCommand command = sameChangeProfileCommand(member.getId());

    given(memberReader.readMemberById(member.getId())).willReturn(member);
    given(memberInterestService.readMemberInterests(member.getId())).willReturn(DEFAULT_INTEREST_DISPLAY_NAMES());

    // when & then
    assertThatThrownBy(() -> memberService.changeProfileProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(IS_SAME_REQUEST.getMessage());

    then(memberInterestService).should().readMemberInterests(member.getId());
    then(memberInterestService).should(never()).changeMemberInterests(any(UUID.class), anyList());

    assertThat(member.getNickname()).isNotNull();
    assertThat(member.isEqualsNickname(command.nickname())).isTrue();
  }

  private static Stream<Arguments> provideChangeProfileSuccessCases() {
    return Stream.of(
        Arguments.of(
            "다른 닉네임",
            "newNickname",
            List.of("interest1", "interest2")
        ),
        Arguments.of(
            "다른 관심사",
            "tester",
            List.of("Kotlin", "Spring")
        )
    );
  }

  @Test
  @DisplayName("임시 비밀번호 발급 - 성공 테스트")
  void 임시_비밀번호_발급을_할_수_있다() {
    // given
    Member member = defaultMember();
    String encodedTempPassword = "$2a$encodedTemp";
    IssuePasswordCommand command = defaultIssuePasswordCommand();
    given(memberReader.readMemberByEmail(member.getEmail())).willReturn(member);
    given(encoder.encode(anyString())).willReturn(encodedTempPassword);

    // when
    memberService.issueTempPasswordProcess(command);

    // then
    then(memberReader).should().readMemberByEmail(member.getEmail());
    then(mailPort).should(times(1)).sendTempPassword(anyString(), anyString());
    then(encoder).should(times(1)).encode(anyString());
    assertThat(member.getPassword()).isEqualTo(encodedTempPassword);
  }

  @Test
  @DisplayName("임시 비밀번호 발급 - 실패 테스트(존재하지 않는 이메일)")
  void 이메일_계정이_존재하지_않으면_임시_비밀번호_발급에_실패한다() {
    // given
    IssuePasswordCommand command = defaultIssuePasswordCommand();
    given(memberReader.readMemberByEmail(command.email())).willThrow(new ApplicationException(NOT_EXISTS_MEMBER));

    // when & then
    assertThatThrownBy(() -> memberService.issueTempPasswordProcess(command))
        .isInstanceOf(RuntimeException.class)
        .hasMessage(NOT_EXISTS_MEMBER.getMessage());
  }

  @Test
  @DisplayName("비밀번호 변경 - 성공 테스트")
  void 기존_비밀번호가_일치하면_비밀번호를_변경할_수_있다() {
    // given
    Member member = defaultIdMember();
    String newPassword = "new-password";
    String encodedNewPassword = "$2a$encodedNew";

    ChangePasswordCommand command = defaultChangePasswordCommand(newPassword);

    given(memberReader.readMemberById(member.getId())).willReturn(member);
    given(encoder.matches(anyString(), anyString())).willReturn(true);
    given(encoder.encode(command.newPassword())).willReturn(encodedNewPassword);

    // when
    memberService.changePasswordProcess(command);

    // then
    then(memberReader).should().readMemberById(member.getId());
    then(encoder).should().matches(defaultMember().getPassword(), command.oldPassword());
    then(encoder).should().encode(newPassword);
    assertThat(member.getPassword()).isEqualTo(encodedNewPassword);
  }

  @Test
  @DisplayName("비밀번호 변경 - 실패 테스트(기존 비밀번호 불일치)")
  void 기존_비밀번호가_일치하지_않으면_비밀번호_변경에_실패한다() {
    // given
    Member member = defaultIdMember();
    String newPassword = "new-password";

    ChangePasswordCommand command = defaultChangePasswordCommand(newPassword);

    given(memberReader.readMemberById(member.getId())).willReturn(member);
    given(encoder.matches(command.oldPassword(), member.getPassword())).willReturn(false);

    // when & then
    assertThatThrownBy(() -> memberService.changePasswordProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(INVALID_OLD_PASSWORD.getMessage());
  }

  @Test
  void 계정_복구() {
    // given
    Member member = removedMember();
    RecoverAccountCommand command = new RecoverAccountCommand(member.getEmail());
    given(memberReader.readMemberByEmail(member.getEmail())).willReturn(member);

    // when
    memberService.recoverMemberAccountProcess(command);

    // then
    assertThat(member.getStatus()).isEqualTo(ACTIVE);
    then(memberReader).should(times(1)).readMemberByEmail(member.getEmail());
    then(eventPublisher).should(times(1)).publishEvent(any(AccountEvent.class));
  }

  @Test
  void 회원_삭제() {
    // given
    Member member = defaultIdMember();
    given(memberReader.readMemberById(member.getId())).willReturn(member);
    MockHttpServletResponse response = new MockHttpServletResponse();
    RemoveMemberCommand command = new RemoveMemberCommand(member.getId());

    // when
    memberService.softRemoveMemberProcess(command, response);

    // then
    assertThat(member.getStatus()).isEqualTo(WITHDRAW);

    then(memberReader).should(times(1)).readMemberById(member.getId());
    then(eventPublisher).should(times(1)).publishEvent(any(AccountEvent.class));

    List<String> setCookies = response.getHeaders("Set-Cookie");
    assertThat(setCookies).anySatisfy(h ->
        assertThat(h).contains("AUTHORIZATION=").contains("Max-Age=0")
    );
    assertThat(setCookies).anySatisfy(h ->
        assertThat(h).contains("REFRESH_KEY=").contains("Max-Age=0")
    );
  }
}
