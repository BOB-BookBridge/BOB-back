package com.bob.domain.chat.service;

import com.bob.domain.chat.entity.ChatRoomMember;
import com.bob.domain.chat.repository.ChatRoomMemberRepository;
import com.bob.domain.chat.service.dto.command.CreateChatRoomMembersCommand;
import com.bob.domain.chat.service.dto.command.ExitChatRoomCommand;
import com.bob.domain.chat.service.dto.command.ReEnterChatRoomCommand;
import com.bob.domain.chat.service.reader.ChatRoomMemberReader;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatRoomMemberService {

  private final ChatRoomMemberRepository chatRoomMemberRepository;
  private final ChatRoomMemberReader chatRoomMemberReader;

  @Transactional
  public void registerChatRoomMembersProcess(CreateChatRoomMembersCommand command) {
    List<ChatRoomMember> chatRoomMembers = command.toChatRoomMembers();
    chatRoomMemberRepository.saveAll(chatRoomMembers);
  }

  @Transactional
  public void reEnterChatRoomMembersProcess(ReEnterChatRoomCommand command) {
    ChatRoomMember member = chatRoomMemberReader.readChatRoomMember(command.chatRoomId(), command.buyerId());
    if (member.getExitedAt() != null) {
      member.reEnterChatRoom(LocalDateTime.now());
    }
  }

  @Transactional
  public void exitChatRoomMemberProcess(ExitChatRoomCommand command) {
    ChatRoomMember member = chatRoomMemberReader.readChatRoomMember(command.chatRoomId(), command.memberId());
    member.updateExitedAt(LocalDateTime.now());
  }
}
