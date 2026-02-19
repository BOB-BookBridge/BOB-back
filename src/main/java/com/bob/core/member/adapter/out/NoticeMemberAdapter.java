package com.bob.core.member.adapter.out;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.admin.notice.application.port.out.NoticeMemberPort;
import com.bob.core.member.application.port.in.MemberReader;

@Component
@RequiredArgsConstructor
public class NoticeMemberAdapter implements NoticeMemberPort {

    private final MemberReader memberReader;

    @Override
    public String readNickname(UUID memberId) {
        return memberReader.readBasicInfo(memberId).nickname();
    }
}
