package com.bob.domain.chat.service;

import static com.bob.global.exception.response.ApplicationError.IS_SAME_CHAT_MEMBER;
import static com.bob.global.exception.response.ApplicationError.NOT_EXISTS_CHAT_PARTNER;
import static com.bob.global.utils.stream.StreamUtils.sortByDesc;

import com.bob.domain.chat.entity.ChatRoom;
import com.bob.domain.chat.repository.ChatRoomRepository;
import com.bob.domain.chat.service.dto.command.CreateChatRoomCommand;
import com.bob.domain.chat.service.dto.command.CreateChatRoomMembersCommand;
import com.bob.domain.chat.service.dto.query.ReadChatRoomListQuery;
import com.bob.domain.chat.service.dto.response.ChatMemberResponse;
import com.bob.domain.chat.service.dto.response.ChatPostResponse;
import com.bob.domain.chat.service.dto.response.ChatRoomSummaryResponse;
import com.bob.domain.chat.service.dto.response.CreateChatRoomResponse;
import com.bob.domain.chat.service.port.out.ChatMemberPort;
import com.bob.domain.chat.service.port.out.ChatPostPort;
import com.bob.domain.chat.service.port.out.ChatTradePort;
import com.bob.domain.chat.service.reader.ChatMessageReader;
import com.bob.domain.chat.service.reader.ChatRoomMemberReader;
import com.bob.domain.chat.service.reader.ChatRoomReader;
import com.bob.domain.chat.usecase.ChatRoomReadUseCase;
import com.bob.domain.chat.usecase.ChatRoomWriteUseCase;
import com.bob.global.exception.exceptions.ApplicationException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatRoomService implements ChatRoomWriteUseCase, ChatRoomReadUseCase {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatRoomReader chatRoomReader;

  private final ChatRoomMemberService chatRoomMemberService;
  private final ChatRoomMemberReader chatRoomMemberReader;

  private final ChatMessageReader chatMessageReader;

  private final ChatPostPort postPort;
  private final ChatTradePort tradePort;
  private final ChatMemberPort memberPort;

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
      throw new ApplicationException(IS_SAME_CHAT_MEMBER);
    }
  }

  @Transactional(readOnly = true)
  public List<ChatRoomSummaryResponse> readChatRoomListProcess(ReadChatRoomListQuery query) {
    List<ChatRoomSummaryResponse> responses = chatRoomReader.readParticipatingChatRoomsByMemberId(query.memberId())
        .stream()
        .filter(ChatRoom::getEnableStatus)
        .map(chatRoom -> convertToChatRoomSummary(query, chatRoom))
        .toList();

    return sortByDesc(responses, ChatRoomSummaryResponse::lastMessageAt);
  }

  private ChatRoomSummaryResponse convertToChatRoomSummary(ReadChatRoomListQuery query, ChatRoom chatRoom) {
    try {
      UUID partnerId = chatRoomMemberReader.readPartnerIdByRequesterId(chatRoom.getId(), query.memberId());
      ChatMemberResponse memberSummary = ChatMemberResponse.from(memberPort.readChatMemberProfile(partnerId));
      ChatPostResponse postSummary = postPort.readChatPostSummary(chatRoom.getPostId());
      int unreadCount = chatMessageReader.readUnreadMessageCountOfChatRoom(chatRoom.getId(), query.memberId());
      return ChatRoomSummaryResponse.from(chatRoom, memberSummary, postSummary, unreadCount);
    } catch (ApplicationException e) {
      if (e.getError() == NOT_EXISTS_CHAT_PARTNER) return null;
      throw e;
    }
  }
}
