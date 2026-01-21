package com.bob.integration.adapter.inquiry;

import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.inquiry.application.port.out.InquiryMemberPort;
import com.bob.core.inquiry.application.port.result.InquiryMember;
import com.bob.core.member.application.dto.result.MemberBasicInfo;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.member.domain.Member;

@Component
@RequiredArgsConstructor
public class InquiryMemberAdapter implements InquiryMemberPort {

    private final MemberReader memberReader;

    @Override
    public InquiryMember read(UUID memberId) {
        MemberBasicInfo info = memberReader.readBasicInfo(memberId);

        return new InquiryMember(info.id(), info.nickname(), info.profileImageUrl());
    }

    @Override
    public Optional<InquiryMember> findByEmail(String email) {
        return memberReader.findByEmail(email)
            .map(member -> new InquiryMember(member.getId(), member.getNickname(), member.getProfileImageUrl()));
    }

    @Override
    public boolean isAuthorized(UUID memberId, String email) {
        Member member = memberReader.read(memberId);

        return member.isAdmin() || member.getEmail().equals(email);
    }
}
