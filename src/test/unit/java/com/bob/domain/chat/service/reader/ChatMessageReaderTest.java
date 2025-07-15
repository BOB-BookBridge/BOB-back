package com.bob.domain.chat.service.reader;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.chat.ChatMessageFixture.DEFAULT_TEXT_CHAT_MESSAGE;
import static com.bob.support.fixture.domain.chat.ChatMessageFixture.WITH_IMAGE_CHAT_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.chat.entity.ChatMessage;
import com.bob.domain.chat.repository.ChatMessageRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("ChatMessageReader 테스트")
@ExtendWith(MockitoExtension.class)
class ChatMessageReaderTest {

  @InjectMocks
  private ChatMessageReader chatMessageReader;

  @Mock
  private ChatMessageRepository chatMessageRepository;

  private Pageable pageable = PageRequest.of(0, 20);

  @Test
  @DisplayName("채팅방 최근 메시지 목록 조회 테스트")
  void 채팅방에서_최근_메시지를_조회한다() {
    // given
    Long chatRoomId = 1L;
    int size = 20;
    LocalDateTime enteredAt = LocalDateTime.of(2024, 1, 1, 12, 0);

    List<ChatMessage> mockMessages = List.of(DEFAULT_TEXT_CHAT_MESSAGE(), WITH_IMAGE_CHAT_MESSAGE());
    given(chatMessageRepository.findRecentMessages(chatRoomId, enteredAt, pageable)).willReturn(mockMessages);

    // when
    List<ChatMessage> result = chatMessageReader.readRecentMessages(chatRoomId, enteredAt, size);

    // then
    assertThat(result).hasSize(2);
    then(chatMessageRepository).should().findRecentMessages(chatRoomId, enteredAt, pageable);
  }

  @Test
  @DisplayName("채팅방 이전 메시지 목록 조회 테스트")
  void 채팅방에서_beforeMessageId_이전_메시지를_조회한다() {
    // given
    Long chatRoomId = 1L;
    Long beforeMessageId = 100L;
    int size = 20;
    LocalDateTime enteredAt = LocalDateTime.of(2025, 7, 14, 12, 0);

    List<ChatMessage> messages = List.of(DEFAULT_TEXT_CHAT_MESSAGE());
    given(chatMessageRepository.findMessagesBeforeId(chatRoomId, beforeMessageId, enteredAt, pageable)).willReturn(messages);

    // when
    List<ChatMessage> result = chatMessageReader.readPreviousMessages(chatRoomId, beforeMessageId, enteredAt, size);

    // then
    assertThat(result).hasSize(1);
    then(chatMessageRepository).should(times(1)).findMessagesBeforeId(chatRoomId, beforeMessageId, enteredAt, pageable);
  }

  @Test
  @DisplayName("읽지 않은 메시지 개수 조회 테스트")
  void 채팅방에서_상대방이_보낸_안_읽은_메시지_개수를_조회한다() {
    // given
    Long chatRoomId = 1L;
    UUID receiverId = MEMBER_ID;
    int unreadCount = 3;

    given(chatMessageRepository.countUnreadMessage(chatRoomId, receiverId)).willReturn(unreadCount);

    // when
    int result = chatMessageReader.readUnreadMessageCountOfChatRoom(chatRoomId, receiverId);

    // then
    assertThat(result).isEqualTo(unreadCount);
    then(chatMessageRepository).should().countUnreadMessage(chatRoomId, receiverId);
  }
}