package com.bob.core.chat.domain;

import static com.bob.core.chat.domain.type.ChatMessageType.IMAGE;
import static com.bob.core.chat.domain.type.ChatMessageType.SYSTEM;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.core.chat.domain.type.ChatMessageType;
import com.bob.shared.entity.AbstractEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Chatroom extends AbstractEntity {

    private Long postId;

    private Long tradeId;

    private String titleSuffix;

    private String lastChatMessage;

    private LocalDateTime createdAt;

    private LocalDateTime lastChatAt;

    @Builder.Default
    private List<ChatroomMember> members = new ArrayList<>();

    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();

    public static Chatroom createChatroom(
        Long postId, Long tradeId, String titleSuffix, UUID memberId1, UUID memberId2
    ) {
        Chatroom chatroom = Chatroom.builder()
            .postId(postId)
            .tradeId(tradeId)
            .titleSuffix(titleSuffix)
            .createdAt(LocalDateTime.now())
            .build();

        ChatroomMember member1 = ChatroomMember.createChatroomMember(memberId1);
        ChatroomMember member2 = ChatroomMember.createChatroomMember(memberId2);

        chatroom.members.addAll(List.of(member1, member2));

        return chatroom;
    }

    public ChatMessage addMessage(UUID senderId, String content, ChatMessageType type) {
        if (type == IMAGE)
            content = "사진";

        ChatMessage message = ChatMessage.createChatMessage(senderId, content, type);
        messages.add(message);

        updateLastMessageInfo(content, message.getCreatedAt());

        return message;
    }

    public ChatMessage addSystemMessage(UUID senderId, String content) {
        ChatMessage message = ChatMessage.createSystemChatMessage(senderId, content);

        messages.add(message);

        return message;
    }

    public void markMessagesAsRead(UUID receiverId) {
        ChatroomMember member = getMember(receiverId);

        messages.stream()
            .filter(message -> !message.getSenderId().equals(receiverId))
            .reduce((first, second) -> second)
            .ifPresent(lastMessage -> member.updateLastReadMessage(lastMessage.getId()));
    }

    private void updateLastMessageInfo(String content, LocalDateTime time) {
        this.lastChatMessage = (content == null || content.isBlank()) ? "사진" : content;
        this.lastChatAt = time;
    }

    public void exitMember(UUID memberId) {
        ChatroomMember member = getMember(memberId);
        member.exit();
    }

    public void reEnterMember(UUID memberId) {
        ChatroomMember member = getMember(memberId);

        if (member.getExitedAt() != null)
            member.enter();
    }

    public boolean hasMember(UUID memberId) {
        return members.stream()
            .anyMatch(member -> member.getMemberId().equals(memberId));
    }

    public boolean isMemberExited(UUID memberId) {
        ChatroomMember member = getMember(memberId);
        return member.getExitedAt() != null;
    }

    public ChatroomMember getMember(UUID memberId) {
        return members.stream()
            .filter(member -> member.getMemberId().equals(memberId))
            .findFirst()
            .get();
    }

    public UUID getPartnerId(UUID myId) {
        return members.stream()
            .map(ChatroomMember::getMemberId)
            .filter(memberId -> !memberId.equals(myId))
            .findFirst()
            .get();
    }

    public List<ChatMessage> getMessagesAfter(LocalDateTime enteredAt) {
        return messages.stream()
            .filter(message -> message.getCreatedAt().isAfter(enteredAt) || message.getCreatedAt().equals(enteredAt))
            .toList();
    }

    public int countUnreadMessages(UUID memberId) {
        ChatroomMember member = getMember(memberId);
        Long lastReadMessageId = member.getLastReadMessageId();

        if (lastReadMessageId == null) {
            return (int)messages.stream()
                .filter(message -> !message.getSenderId().equals(memberId))
                .filter(message -> message.getType() != SYSTEM)
                .count();
        }

        return (int)messages.stream()
            .filter(message -> !message.getSenderId().equals(memberId) && message.getId() > lastReadMessageId)
            .filter(message -> message.getType() != SYSTEM)
            .count();
    }

    public void reEnterAllMembers() {
        members.forEach(member -> {
            if (member.getExitedAt() != null)
                member.enter();
        });
    }
}
