package com.bob.domain.member.service;

import static com.bob.domain.member.entity.Status.ACTIVE;
import static com.bob.domain.member.entity.Status.BANNED;
import static com.bob.domain.member.entity.Status.WITHDRAW;
import static com.bob.global.event.application.dto.member.type.AccountEventType.RECOVER;
import static com.bob.global.exception.response.ApplicationError.ALREADY_EXISTS_EMAIL;
import static com.bob.global.exception.response.ApplicationError.INVALID_OLD_PASSWORD;
import static com.bob.global.exception.response.ApplicationError.NO_CHANGES;
import static com.bob.global.exception.response.ApplicationError.UNVERIFIED_EMAIL;
import static com.bob.global.utils.random.RandomUtils.generateCode;
import static com.bob.global.utils.web.CookieUtils.removeCookie;

import com.bob.domain.member.entity.Member;
import com.bob.domain.member.repository.MemberRepository;
import com.bob.domain.member.service.dto.command.ChangePasswordCommand;
import com.bob.domain.member.service.dto.command.ChangeProfileCommand;
import com.bob.domain.member.service.dto.command.ChangeProfileImageCommand;
import com.bob.domain.member.service.dto.command.CreateMemberCommand;
import com.bob.domain.member.service.dto.command.IssuePasswordCommand;
import com.bob.domain.member.service.dto.command.RecoverAccountCommand;
import com.bob.domain.member.service.dto.command.RemoveMemberCommand;
import com.bob.domain.member.service.dto.command.SocialLoginCommand;
import com.bob.domain.member.service.dto.query.ReadMemberBooksQuery;
import com.bob.domain.member.service.dto.query.ReadMemberWishesQuery;
import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberAreaSummaryResponse;
import com.bob.domain.member.service.dto.response.MemberBooksResponse;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.service.dto.response.SocialLoginResponse;
import com.bob.domain.member.service.dto.response.internal.MemberWishSummary;
import com.bob.domain.member.service.port.out.MemberAreaPort;
import com.bob.domain.member.service.port.out.MemberMailPort;
import com.bob.domain.member.service.port.out.MemberRedisPort;
import com.bob.domain.member.service.reader.MemberReader;
import com.bob.domain.member.usecase.MemberModifyUseCase;
import com.bob.domain.member.usecase.MemberReadUseCase;
import com.bob.domain.member.usecase.MemberWriteUseCase;
import com.bob.global.event.application.dto.member.AccountEvent;
import com.bob.global.event.application.dto.member.type.AccountEventType;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService implements MemberWriteUseCase, MemberReadUseCase, MemberModifyUseCase {

  private final MemberRepository memberRepository;
  private final MemberReader memberReader;

  private final MemberInterestService memberInterestService;
  private final MemberBookService memberBookService;
  private final MemberWishService memberWishService;

  private final MemberAreaPort areaPort;
  private final MemberMailPort mailPort;
  private final MemberRedisPort redisPort;

  private final PasswordEncoder encoder;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void signupProcess(CreateMemberCommand command) {
    verifyEmail(command.email());
    Member member = command.toMember(encoder.encode(command.password()));
    memberRepository.save(member);
    areaPort.createMemberActivityArea(member.getId(), command.emdId());
  }

  private void verifyEmail(String email) {
    validateEmailNotExists(email);
    validateEmailIsVerified(email);
    redisPort.deleteVerified(email);
  }

  private void validateEmailNotExists(String email) {
    if (memberRepository.existsByEmail(email)) {
      throw new ApplicationException(ALREADY_EXISTS_EMAIL);
    }
  }

  private void validateEmailIsVerified(String email) {
    if (!redisPort.isVerified(email)) {
      throw new ApplicationException(UNVERIFIED_EMAIL);
    }
  }

  @Transactional
  public SocialLoginResponse socialLoginProcess(SocialLoginCommand command) {
    return memberRepository.findByEmail(command.email())
        .map(SocialLoginResponse::from)
        .orElseGet(() -> {
          Member member = memberRepository.save(command.toMember(encoder.encode(generateCode(12))));
          areaPort.createNonAuthenticatedActivityArea(member.getId(), command.emdId());
          return SocialLoginResponse.from(member);
        });
  }

  @Transactional(readOnly = true)
  public MemberProfileResponse readProfileProcess(ReadProfileQuery query) {
    Member member = memberReader.readMemberById(query.memberId());
    List<String> interests = memberInterestService.readMemberInterests(member.getId());
    List<MemberWishSummary> wishes = memberWishService.readWishesProcess(ReadMemberWishesQuery.of(member.getId())).wishes();
    MemberAreaSummaryResponse area = areaPort.readMemberAreaSummary(query.memberId());
    MemberBooksResponse books = memberBookService.readMemberBooksProcess(ReadMemberBooksQuery.of(member.getId()));
    return MemberProfileResponse.from(member, interests, area, books.bookcase(), wishes);
  }

  @Transactional
  public void changeProfileProcess(ChangeProfileCommand command) {
    Member member = memberReader.readMemberById(command.memberId());
    List<String> interests = memberInterestService.readMemberInterests(member.getId());
    verifyIsSameRequest(member, command.nickname(), interests, command.interests());
    member.updateNickname(command.nickname());
    memberInterestService.changeMemberInterests(member.getId(), command.interests());
  }

  private static void verifyIsSameRequest(Member member, String nickname, List<String> oldInterests, List<String> interests) {
    Set<String> oldLower = oldInterests.stream().map(String::toLowerCase).collect(Collectors.toSet());
    Set<String> newLower = interests.stream().map(String::toLowerCase).collect(Collectors.toSet());
    if (member.isEqualsNickname(nickname) && Objects.equals(oldLower, newLower))
      throw new ApplicationException(NO_CHANGES);
  }

  @Transactional
  public void changePasswordProcess(ChangePasswordCommand command) {
    Member member = memberReader.readMemberById(command.memberId());
    verifyPassword(member.getPassword(), command.oldPassword());
    member.updatePassword(encoder.encode(command.newPassword()));
  }

  private void verifyPassword(String memberPassword, String receiveOldPassword) {
    if (!encoder.matches(receiveOldPassword, memberPassword)) {
      throw new ApplicationException(INVALID_OLD_PASSWORD);
    }
  }

  @Transactional
  public void issueTempPasswordProcess(IssuePasswordCommand command) {
    Member member = memberReader.readMemberByEmail(command.email());
    String tempPassword = generateCode(12);
    mailPort.sendTempPassword(command.email(), tempPassword);
    member.updatePassword(encoder.encode(tempPassword));
  }

  @Transactional
  public void changeMemberProfileImageProcess(ChangeProfileImageCommand command) {
    Member member = memberReader.readMemberById(command.memberId());
    member.updateProfileImageUrl(command.fileName());
  }

  @Transactional
  public void recoverMemberAccountProcess(RecoverAccountCommand command) {
    Member member = memberReader.readMemberByEmail(command.email());
    verifyIsNotBannedMember(member);
    member.updateStatus(ACTIVE);

    eventPublisher.publishEvent(AccountEvent.of(member.getId(), RECOVER));
  }

  private static void verifyIsNotBannedMember(Member member) {
    if (member.getStatus() == BANNED) {
      throw new ApplicationException(ApplicationError.IS_BANNED_MEMBER);
    }
  }

  @Transactional
  public void softRemoveMemberProcess(RemoveMemberCommand command, HttpServletResponse response) {
    Member member = memberReader.readMemberById(command.memberId());
    member.updateStatus(WITHDRAW);
    eventPublisher.publishEvent(AccountEvent.of(member.getId(), AccountEventType.WITHDRAW));
    removeCookie(response, "AUTHORIZATION");
    removeCookie(response, "REFRESH_KEY");
  }
}
