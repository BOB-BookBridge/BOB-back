package com.bob.domain.chat.service.reader;

import static com.bob.global.exception.response.ApplicationError.NOT_PARTICIPATED_CHAT_ROOM;

import com.bob.domain.chat.entity.ChatRoomMember;
import com.bob.domain.chat.repository.ChatRoomMemberRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatRoomMemberReader {

  private final ChatRoomMemberRepository chatRoomMemberRepository;

  public ChatRoomMember readChatRoomMember(Long chatRoomId, UUID memberId) {
    return chatRoomMemberRepository.findByChatRoomIdAndMemberId(chatRoomId, memberId)
        .orElseThrow(() -> new ApplicationException(NOT_PARTICIPATED_CHAT_ROOM));
  }

  public UUID readPartnerIdByRequesterId(Long chatRoomId, UUID requesterId) {
    return chatRoomMemberRepository.findPartnerIdByRequesterId(chatRoomId, requesterId)
        .orElseThrow(() -> new ApplicationException(ApplicationError.NOT_EXISTS_CHAT_PARTNER));
  }

  public List<UUID> readChatRoomMemberIds(Long chatRoomId) {
    return chatRoomMemberRepository.findByChatRoomId(chatRoomId)
        .stream()
        .map(ChatRoomMember::getMemberId)
        .toList();
  }
}
