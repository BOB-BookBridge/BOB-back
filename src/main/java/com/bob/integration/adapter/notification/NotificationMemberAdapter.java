package com.bob.integration.adapter.notification;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.member.application.dto.result.MemberBasicInfo;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.notification.application.port.out.NotificationMemberPort;
import com.bob.core.notification.application.port.result.NotificationMember;

@Component
@RequiredArgsConstructor
public class NotificationMemberAdapter implements NotificationMemberPort {

    private final MemberReader memberReader;

    @Override
    public NotificationMember read(UUID memberId) {
        MemberBasicInfo info = memberReader.readBasicInfo(memberId);

        return new NotificationMember(info.id(), info.nickname(), info.profileImageUrl());
    }

    @Override
    public List<UUID> readAllMemberIds() {
        return memberReader.readAllActiveMemberIds();
    }
}
