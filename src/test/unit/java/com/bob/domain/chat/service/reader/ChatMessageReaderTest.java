package com.bob.domain.chat.service.reader;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.chat.repository.ChatMessageRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("ChatMessageReader 테스트")
@ExtendWith(MockitoExtension.class)
class ChatMessageReaderTest {

  @InjectMocks
  private ChatMessageReader chatMessageReader;

  @Mock
  private ChatMessageRepository chatMessageRepository;

  @Test
  @DisplayName("읽지 않은 메시지 개수 조회 테스트")
  void 채팅방에서_상대방이_보낸_안읽은_메시지_개수를_조회한다() {
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