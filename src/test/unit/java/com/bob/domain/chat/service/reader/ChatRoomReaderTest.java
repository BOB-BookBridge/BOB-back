package com.bob.domain.chat.service.reader;

import static com.bob.support.fixture.command.CreateChatRoomMembersCommandFixture.CHAT_ROOM_ID;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.domain.chat.ChatRoomFixture.DEFAULT_CHAT_ROOM_1;
import static com.bob.support.fixture.domain.chat.ChatRoomFixture.DEFAULT_CHAT_ROOM_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.chat.entity.ChatRoom;
import com.bob.domain.chat.repository.ChatRoomRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("채팅방 Reader 테스트")
@ExtendWith(MockitoExtension.class)
class ChatRoomReaderTest {

  @InjectMocks
  private ChatRoomReader chatRoomReader;

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Test
  @DisplayName("채팅방 ID를 통한 채팅방 조회 - 성공 테스트")
  void 채팅방ID로_조회_성공() {
    // given
    Long chatRoomId = CHAT_ROOM_ID;
    ChatRoom chatRoom = DEFAULT_CHAT_ROOM_1();

    given(chatRoomRepository.findById(chatRoomId)).willReturn(Optional.of(chatRoom));

    // when
    ChatRoom result = chatRoomReader.readChatRoomById(chatRoomId);

    // then
    assertThat(result).isEqualTo(chatRoom);
    then(chatRoomRepository).should().findById(chatRoomId);
  }

  @Test
  @DisplayName("채팅방 ID를 통한 채팅방 조회 - 실패 테스트")
  void 채팅방ID로_조회_실패_예외발생() {
    // given
    Long chatRoomId = 999L;

    given(chatRoomRepository.findById(chatRoomId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> chatRoomReader.readChatRoomById(chatRoomId))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_EXISTS_CHAT_ROOM.getMessage());

    then(chatRoomRepository).should().findById(chatRoomId);
  }

  @Test
  @DisplayName("참여 중인 채팅방 목록 조회 테스트")
  void 회원이_참여중인_채팅방_목록을_조회할_수_있다() {
    // given
    UUID memberId = MEMBER_ID;
    ChatRoom chatRoom1 = DEFAULT_CHAT_ROOM_1();
    ChatRoom chatRoom2 = DEFAULT_CHAT_ROOM_2();
    List<ChatRoom> expectedChatRooms = List.of(chatRoom1, chatRoom2);

    given(chatRoomRepository.findAllByMemberId(memberId)).willReturn(expectedChatRooms);

    // when
    List<ChatRoom> result = chatRoomReader.readParticipatingChatRoomsByMemberId(memberId);

    // then
    assertThat(result).hasSize(2).containsExactlyInAnyOrder(chatRoom1, chatRoom2);
    then(chatRoomRepository).should().findAllByMemberId(memberId);
  }

  @Test
  @DisplayName("채팅방 ID 조회 테스트")
  void 동일한_게시글_판매자_구매자의_경우_이미_존재하는_채팅방_ID를_반환한다() {
    // given
    Long postId = 1L;
    UUID sellerId = MEMBER_ID;
    UUID buyerId = OTHER_MEMBER_ID;

    given(chatRoomRepository.findExistingChatRoom(postId, sellerId, buyerId)).willReturn(Optional.of(CHAT_ROOM_ID));

    // when
    Optional<Long> result = chatRoomReader.readExistingChatRoom(postId, sellerId, buyerId);

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(CHAT_ROOM_ID);
    then(chatRoomRepository).should().findExistingChatRoom(postId, sellerId, buyerId);
  }
}