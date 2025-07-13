package com.bob.domain.chat.service;

import static com.bob.global.event.sse.manager.type.EmitterType.CHAT;
import static com.bob.support.fixture.command.CreateChatMessageCommandFixture.WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.domain.chat.ChatMessageFixture.DEFAULT_TEXT_CHAT_MESSAGE;
import static com.bob.support.fixture.domain.chat.ChatMessageFixture.WITH_IMAGE_CHAT_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bob.domain.chat.entity.ChatMessage;
import com.bob.domain.chat.repository.ChatMessageRepository;
import com.bob.domain.chat.service.dto.command.CreateChatMessageCommand;
import com.bob.domain.chat.service.dto.command.EnterChatRoomCommand;
import com.bob.domain.chat.service.port.out.ChatFilePort;
import com.bob.global.event.sse.manager.EmitterManager;
import com.bob.global.event.sse.repository.chat.ChatEmitterKey;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("채팅 메시지 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

  @InjectMocks
  private ChatMessageService chatMessageService;

  @Mock
  private ChatMessageRepository chatMessageRepository;

  @Mock
  private ChatFilePort chatFilePort;

  @Mock
  private EmitterManager emitterManager;

  @Test
  @DisplayName("채팅 메시지 저장 및 이미지 존재 시 매핑 테스트")
  void 채팅_메시지를_저장하고_이미지를_매핑할_수_있다() {
    // given
    CreateChatMessageCommand command = WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND();
    ChatMessage expected = WITH_IMAGE_CHAT_MESSAGE();
    given(chatMessageRepository.save(any())).willReturn(expected);

    // when
    ChatMessage result = chatMessageService.createChatMessageProcess(command, OTHER_MEMBER_ID);

    // then
    assertThat(result).isNotNull();
    verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
    verify(chatFilePort, times(1)).modifyReferenceId(command.fileNames(), String.valueOf(result.getId()));
  }

  @Test
  @DisplayName("상대방 채팅방 입장 상태인 경우 메시지 읽음 처리 테스트")
  void 상대방이_채팅방에_입장_한_상태면_메시지는_읽음_처리된다() {
    // given
    CreateChatMessageCommand command = WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND();
    ChatMessage expected = WITH_IMAGE_CHAT_MESSAGE();
    given(chatMessageRepository.save(any())).willReturn(expected);
    given(emitterManager.isExistClientConnection(CHAT,
        ChatEmitterKey.of(command.chatRoomId(), OTHER_MEMBER_ID))).willReturn(true);

    // when
    ChatMessage result = chatMessageService.createChatMessageProcess(command, OTHER_MEMBER_ID);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getIsRead()).isTrue();
    verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
    verify(chatFilePort, times(1)).modifyReferenceId(command.fileNames(), String.valueOf(result.getId()));
  }

  @Test
  @DisplayName("채팅방 입장 시 읽지 않은 메시지를 읽음 처리한다")
  void 채팅방_입장시_읽지_않은_메시지를_읽음처리한다() {
    // given
    Long chatRoomId = 1L;
    UUID memberId = UUID.randomUUID();
    EnterChatRoomCommand command = new EnterChatRoomCommand(chatRoomId, memberId);
    ChatMessage unread1 = DEFAULT_TEXT_CHAT_MESSAGE();
    ChatMessage unread2 = WITH_IMAGE_CHAT_MESSAGE();
    given(chatMessageRepository.findUnreadMessages(chatRoomId, memberId)).willReturn(List.of(unread1, unread2));

    // when
    chatMessageService.updateReadStatusProcess(command);

    // then
    assertThat(unread1.getIsRead()).isTrue();
    assertThat(unread2.getIsRead()).isTrue();
  }

  @Test
  @DisplayName("fileNames가 null이면 이미지 매핑을 수행하지 않는다")
  void fileNames가_null이면_이미지_매핑이_수행되지_않는다() {
    // given
    CreateChatMessageCommand command = new CreateChatMessageCommand(
        1L,
        UUID.randomUUID(),
        "메시지",
        null
    );
    ChatMessage expected = DEFAULT_TEXT_CHAT_MESSAGE();
    given(chatMessageRepository.save(any())).willReturn(expected);

    // when
    ChatMessage result = chatMessageService.createChatMessageProcess(command, OTHER_MEMBER_ID);

    // then
    assertThat(result).isNotNull();
    verify(chatMessageRepository).save(any(ChatMessage.class));
    verify(chatFilePort, never()).modifyReferenceId(any(), any());
  }

  @Test
  @DisplayName("fileNames가 빈 리스트이면 이미지 매핑을 수행하지 않는다")
  void fileNames가_빈_리스트이면_이미지_매핑이_수행되지_않는다() {
    // given
    CreateChatMessageCommand command = new CreateChatMessageCommand(
        1L,
        UUID.randomUUID(),
        "메시지",
        List.of()
    );
    ChatMessage expected = DEFAULT_TEXT_CHAT_MESSAGE();
    given(chatMessageRepository.save(any())).willReturn(expected);

    // when
    ChatMessage result = chatMessageService.createChatMessageProcess(command, OTHER_MEMBER_ID);

    // then
    assertThat(result).isNotNull();
    verify(chatMessageRepository).save(any(ChatMessage.class));
    verify(chatFilePort, never()).modifyReferenceId(any(), any());
  }
}
