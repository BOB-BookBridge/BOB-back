package com.bob.domain.chat.service.dto.command;

import com.bob.domain.chat.entity.ChatRoomMember;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateChatRoomMembersCommand(
    Long chatRoomId,
    List<UUID> memberIds
) {

  public static CreateChatRoomMembersCommand of(Long chatRoomID, List<UUID> memberIds) {
    return new CreateChatRoomMembersCommand(chatRoomID, memberIds);
  }

  public List<ChatRoomMember> toChatRoomMembers() {
    return memberIds.stream()
        .map((memberId) -> ChatRoomMember
            .builder()
            .chatRoomId(chatRoomId)
            .memberId(memberId)
            .enteredAt(LocalDateTime.now())
            .build()
        ).toList();
  }
}