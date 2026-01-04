package com.bob.integration.adapter.chat;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.chat.application.port.out.ChatMemberPort;
import com.bob.core.chat.application.port.result.ChatMember;
import com.bob.core.member.application.dto.result.MemberBasicInfo;
import com.bob.core.member.application.port.in.MemberReader;

@Component
@RequiredArgsConstructor
public class ChatMemberAdapter implements ChatMemberPort {

    private final MemberReader memberReader;

    @Override
    public ChatMember read(UUID memberId) {
        MemberBasicInfo info = memberReader.readBasicInfo(memberId);

        return ChatMember.of(info.id(), info.nickname(), info.profileImageUrl());
    }
}
