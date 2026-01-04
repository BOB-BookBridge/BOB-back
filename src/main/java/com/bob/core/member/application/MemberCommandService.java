package com.bob.core.member.application;

import static com.bob.core.member.domain.Member.createMember;
import static com.bob.core.member.domain.Member.createSocialMember;
import static com.bob.global.exception.response.ApplicationError.MEMBER_BANNED;
import static com.bob.global.exception.response.ApplicationError.MEMBER_EMAIL_DUPLICATED;
import static com.bob.global.exception.response.ApplicationError.MEMBER_EMAIL_UNVERIFIED;
import static com.bob.global.exception.response.ApplicationError.MEMBER_PASSWORD_MISMATCH;
import static com.bob.global.exception.response.ApplicationError.NO_CHANGES;
import static com.bob.global.utils.random.RandomUtils.generateCode;
import static com.bob.global.utils.web.CookieUtils.removeCookie;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.member.application.dto.command.ChangePasswordCommand;
import com.bob.core.member.application.dto.command.ChangeProfileCommand;
import com.bob.core.member.application.dto.command.ChangeProfileImageCommand;
import com.bob.core.member.application.dto.command.ChangeStatusCommand;
import com.bob.core.member.application.dto.command.CreateMemberCommand;
import com.bob.core.member.application.dto.command.SocialLoginCommand;
import com.bob.core.member.application.port.in.MemberModifier;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.member.application.port.in.MemberRegister;
import com.bob.core.member.application.port.out.MemberAreaPort;
import com.bob.core.member.application.port.out.MemberInterestPort;
import com.bob.core.member.application.port.out.infra.MailSender;
import com.bob.core.member.application.port.out.infra.MemberCachePort;
import com.bob.core.member.domain.Member;
import com.bob.core.member.domain.MemberArea;
import com.bob.core.member.domain.MemberInterest;
import com.bob.core.member.domain.Status;
import com.bob.core.member.domain.encoder.PasswordEncoder;
import com.bob.core.member.domain.repository.MemberRepository;
import com.bob.core.member.event.MemberDeactivatedEvent;
import com.bob.core.member.event.MemberRecoveredEvent;
import com.bob.global.exception.exceptions.ApplicationException;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService implements MemberRegister, MemberModifier {

    private final MemberRepository memberRepository;
    private final MemberReader memberReader;

    private final MailSender mailSender;
    private final MemberCachePort cachePort;

    private final MemberAreaPort areaPort;
    private final MemberInterestPort interestPort;

    private final PasswordEncoder encoder;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Member signup(CreateMemberCommand command) {
        verifyEmailConfirm(command.email());
        verifyEmailDuplicate(command.email());

        String password = encoder.encode(command.password());

        Member member = createMember(command.email(), password, command.nickname(), command.emdId());

        cachePort.delete(command.email());

        return memberRepository.save(member);
    }

    private void verifyEmailConfirm(String email) {
        if (!cachePort.checkAuthenticationSuccess(email))
            throw new ApplicationException(MEMBER_EMAIL_UNVERIFIED);
    }

    private void verifyEmailDuplicate(String email) {
        if (memberRepository.existsByEmail(email))
            throw new ApplicationException(MEMBER_EMAIL_DUPLICATED);
    }

    @Override
    public Member socialLogin(SocialLoginCommand command) {
        String email = command.email();

        return memberRepository.findByEmail(email)
            .orElseGet(() -> memberRepository.save(createSocialMember(email, command.provider(), command.nickname())));
    }

    @Override
    public Member changeStatus(UUID memberId, ChangeStatusCommand command) {
        Status status = Status.valueOf(command.status());

        Member member = memberReader.read(memberId);
        member.updateStatusFromAdmin(status, command.memo());

        return memberRepository.save(member);
    }

    @Override
    public Member changeProfile(UUID memberId, ChangeProfileCommand command) {
        Member member = memberReader.read(memberId);

        List<Long> interestIds = interestPort.registerAll(command.interests());

        verifyIsSameRequest(member, command, interestIds);

        int emdId = member.getArea().getEmdId();

        if (command.authenticateArea()) {
            areaPort.authenticate(command.emdId(), command.lat(), command.lon());
            emdId = command.emdId();
        }

        member.updateInfo(command.nickname(), emdId, interestIds, command.interests());

        return member;
    }

    private void verifyIsSameRequest(Member member, ChangeProfileCommand command, List<Long> newInterestIds) {
        Set<Long> old = member.getInterests().stream().map((MemberInterest::getInterestId)).collect(Collectors.toSet());
        boolean interestsUnchanged = Objects.equals(old, new HashSet<>(newInterestIds));

        boolean nicknameUnchanged = Objects.equals(member.getNickname(), command.nickname());

        MemberArea area = member.getArea();
        boolean areaUnchanged = !command.authenticateArea() && Objects.equals(area.getEmdId(), command.emdId());

        if (interestsUnchanged && nicknameUnchanged && areaUnchanged)
            throw new ApplicationException(NO_CHANGES);
    }

    @Override
    public Member changePassword(UUID memberId, ChangePasswordCommand command) {
        Member member = memberReader.read(memberId);

        verifyPasswordMatches(member.getPassword(), command.oldPassword());

        member.updatePassword(encoder.encode(command.newPassword()));

        return member;
    }

    private void verifyPasswordMatches(String password, String oldPassword) {
        if (!encoder.matches(oldPassword, password))
            throw new ApplicationException(MEMBER_PASSWORD_MISMATCH);
    }

    @Override
    public Member changeProfileImage(UUID memberId, ChangeProfileImageCommand command) {
        Member member = memberReader.read(memberId);

        member.updateProfileImageUrl(command.fileName());

        return member;
    }

    @Override
    public Member issueTempPassword(String email) {
        Member member = memberReader.read(email);

        String tempPassword = generateCode(12);
        mailSender.send(email, "임시 비밀번호", tempPassword);

        member.updatePassword(encoder.encode(tempPassword));

        return member;
    }

    @Override
    public Member activate(String email) {
        Member member = memberReader.read(email);

        verifyIsNotBannedMember(member);

        member.activate();

        eventPublisher.publishEvent(new MemberRecoveredEvent(member.getId()));

        return member;
    }

    private static void verifyIsNotBannedMember(Member member) {
        if (member.isBanned())
            throw new ApplicationException(MEMBER_BANNED);
    }

    @Override
    public Member deactivate(UUID memberId, HttpServletResponse response) {
        Member member = memberReader.read(memberId);

        member.deactivate();

        removeCookie(response, "AUTHORIZATION");
        removeCookie(response, "REFRESH_KEY");

        eventPublisher.publishEvent(new MemberDeactivatedEvent(member.getId()));

        return member;
    }
}
