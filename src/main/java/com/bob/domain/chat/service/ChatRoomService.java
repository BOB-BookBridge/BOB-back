package com.bob.domain.chat.service;

import com.bob.domain.chat.entity.ChatRoom;
import com.bob.domain.chat.repository.ChatRoomRepository;
import com.bob.domain.chat.service.dto.command.CreateChatRoomCommand;
import com.bob.domain.chat.service.dto.command.CreateChatRoomMembersCommand;
import com.bob.domain.chat.service.dto.response.ChatPostResponse;
import com.bob.domain.chat.service.dto.response.CreateChatRoomResponse;
import com.bob.domain.chat.service.port.out.ChatPostPort;
import com.bob.domain.chat.service.port.out.ChatTradePort;
import com.bob.domain.chat.service.reader.ChatRoomReader;
import com.bob.domain.chat.usecase.ChatRoomWriteUseCase;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatRoomService implements ChatRoomWriteUseCase {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatRoomReader chatRoomReader;

  private final ChatRoomMemberService chatRoomMemberService;

  private final ChatPostPort postPort;
  private final ChatTradePort tradePort;

  @Transactional
  public CreateChatRoomResponse createChatRoomProcess(CreateChatRoomCommand command) {
    ChatPostResponse post = postPort.readChatPostSummary(command.postId());
    verifyBuyer(post.sellerId(), command.buyerId());

    return chatRoomReader.readExistingChatRoom(post.postId(), post.sellerId(), command.buyerId())
        .map(CreateChatRoomResponse::of)
        .orElseGet(() -> createNewChatRoom(command, post));
  }

  private CreateChatRoomResponse createNewChatRoom(CreateChatRoomCommand command, ChatPostResponse post) {
    Long tradeId = tradePort.createTrade(post.postId(), post.sellerId(), command.buyerId());
    ChatRoom chatRoom = command.toChatRoom(tradeId, post.title());
    chatRoomRepository.save(chatRoom);
    chatRoomMemberService.registerChatRoomMembers(CreateChatRoomMembersCommand.of(
        chatRoom.getId(),
        List.of(post.sellerId(), command.buyerId())
    ));
    return CreateChatRoomResponse.of(chatRoom.getId());
  }

  private void verifyBuyer(UUID sellerId, UUID buyerId) {
    if (Objects.equals(sellerId, buyerId)) {
      throw new ApplicationException(ApplicationError.IS_SAME_CHAT_MEMBER);
    }
  }
}
