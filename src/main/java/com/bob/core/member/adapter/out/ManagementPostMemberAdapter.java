package com.bob.core.member.adapter.out;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.admin.post.application.port.out.ManagementPostMemberPort;
import com.bob.core.member.application.port.in.MemberReader;

@Component
@RequiredArgsConstructor
public class ManagementPostMemberAdapter implements ManagementPostMemberPort {

    private final MemberReader memberReader;

    @Override
    public String readNickname(UUID memberId) {
        return memberReader.readBasicInfo(memberId).nickname();
    }
}
