package com.bob.integration.adapter.chat;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.chat.port.out.ChatMemberPort;
import com.bob.core.application.chat.port.result.ChatMember;
import com.bob.core.application.member.dto.result.MemberDetail;
import com.bob.core.application.member.port.in.MemberReader;

@Component
@RequiredArgsConstructor
public class ChatMemberAdapter implements ChatMemberPort {

    private final MemberReader memberReader;

    @Override
    public ChatMember read(UUID memberId) {
        MemberDetail detail = memberReader.readDetail(memberId, false);

        return ChatMember.of(detail.id(), detail.nickname(), detail.profileImageUrl());
    }
}
