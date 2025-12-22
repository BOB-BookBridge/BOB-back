package com.bob.core.chat.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("채팅방 멤버 도메인 테스트")
class ChatroomMemberTest {

    @Test
    void 채팅방_멤버_생성() {
        UUID memberId = UUID.randomUUID();

        ChatroomMember member = ChatroomMember.createChatroomMember(memberId);

        assertThat(member.getMemberId()).isEqualTo(memberId);
        assertThat(member.getEnteredAt()).isNotNull();
        assertThat(member.getExitedAt()).isNull();
    }

    @Test
    void 채팅방_퇴장() {
        ChatroomMember member = ChatroomMember.createChatroomMember(UUID.randomUUID());
        LocalDateTime now = LocalDateTime.now();

        member.exit();

        assertThat(member.getExitedAt()).isCloseTo(now, within(1, ChronoUnit.SECONDS));
    }

    @Test
    void 채팅방_재입장() {
        ChatroomMember member = ChatroomMember.createChatroomMember(UUID.randomUUID());

        member.exit();
        LocalDateTime now = LocalDateTime.now();
        member.enter();

        assertThat(member.getEnteredAt()).isCloseTo(now, within(1, ChronoUnit.SECONDS));
        assertThat(member.getExitedAt()).isNull();
    }
}
