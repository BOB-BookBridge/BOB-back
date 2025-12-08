package com.bob.core.adapter.member.out;

import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.member.dto.command.SocialLoginCommand;
import com.bob.core.application.member.port.in.MemberReader;
import com.bob.core.application.member.port.in.MemberRegister;
import com.bob.core.domain.member.Member;
import com.bob.security.application.port.dto.AuthMember;
import com.bob.security.application.port.dto.SocialAuthMember;
import com.bob.security.application.port.out.MemberLoader;

@Component
@RequiredArgsConstructor
public class MemberLoaderAdapter implements MemberLoader {

    private final MemberRegister memberRegister;
    private final MemberReader memberReader;

    @Override
    public Optional<AuthMember> load(String email) {
        try {
            Member member = memberReader.read(email);
            return Optional.of(AuthMember.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .status(member.getStatus().name())
                .role(member.getRole().name())
                .build()
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public AuthMember load(UUID id) {
        Member member = memberReader.read(id);

        return AuthMember.builder()
            .id(member.getId())
            .email(member.getEmail())
            .password(member.getPassword())
            .status(member.getStatus().name())
            .role(member.getRole().name())
            .build();
    }

    @Override
    public SocialAuthMember load(String provider, String email, String nickname) {
        SocialLoginCommand command = SocialLoginCommand.of(provider, email, nickname);
        Member member = memberRegister.socialLogin(command);
        return SocialAuthMember.of(member.getId(), member.getStatus().name());
    }
}
