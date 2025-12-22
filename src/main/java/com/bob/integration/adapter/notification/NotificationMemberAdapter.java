package com.bob.integration.adapter.notification;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.member.application.dto.result.MemberDetail;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.notification.application.port.out.NotificationMemberPort;
import com.bob.core.notification.application.port.result.NotificationMember;

@Component
@RequiredArgsConstructor
public class NotificationMemberAdapter implements NotificationMemberPort {

    private final MemberReader memberReader;

    @Override
    public NotificationMember read(UUID memberId) {
        MemberDetail detail = memberReader.readDetail(memberId, false);

        return new NotificationMember(detail.id(), detail.nickname(), detail.profileImageUrl());
    }
}
