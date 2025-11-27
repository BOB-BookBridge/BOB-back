package com.bob.core.domain.chat;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.core.domain.AbstractEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatroomMember extends AbstractEntity {

    private UUID memberId;

    private Long lastReadMessageId;

    private LocalDateTime enteredAt;

    private LocalDateTime exitedAt;

    public static ChatroomMember createChatroomMember(UUID memberId) {
        return ChatroomMember.builder()
            .memberId(memberId)
            .enteredAt(LocalDateTime.now())
            .build();
    }

    public void exit() {
        this.exitedAt = LocalDateTime.now();
    }

    public void enter() {
        this.enteredAt = LocalDateTime.now();
        this.exitedAt = null;
    }

    public void updateLastReadMessage(Long messageId) {
        this.lastReadMessageId = messageId;
    }
}
