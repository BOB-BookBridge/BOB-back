package com.bob.integration.adapter.inquiry;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.inquiry.application.port.out.InquiryMemberPort;
import com.bob.core.member.application.dto.result.MemberBasicInfo;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.member.domain.Member;

@Component
@RequiredArgsConstructor
public class InquiryMemberAdapter implements InquiryMemberPort {

    private final MemberReader memberReader;

    @Override
    public String readNickname(UUID memberId) {
        MemberBasicInfo info = memberReader.readBasicInfo(memberId);

        return info.nickname();
    }

    public boolean isAuthorized(UUID memberId, String email) {
        Member member = memberReader.read(memberId);

        return member.isAdmin() || member.getEmail().equals(email);
    }
}
