package com.bob.domain.chat.service.reader;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.domain.chat.ChatRoomMemberFixture.CHAT_ROOM_MEMBER_1;
import static com.bob.support.fixture.domain.chat.ChatRoomMemberFixture.CHAT_ROOM_MEMBER_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.chat.repository.ChatRoomMemberRepository;
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

@DisplayName("ChatRoomMemberReader 테스트")
@ExtendWith(MockitoExtension.class)
class ChatRoomMemberReaderTest {

  @InjectMocks
  private ChatRoomMemberReader chatRoomMemberReader;

  @Mock
  private ChatRoomMemberRepository chatRoomMemberRepository;

  @Test
  @DisplayName("상대방 ID 조회 - 성공 테스트")
  void 요청자를_제외한_상대방_ID를_정상적으로_조회할_수_있다() {
    // given
    Long chatRoomId = 1L;
    UUID requesterId = MEMBER_ID;
    UUID partnerId = OTHER_MEMBER_ID;

    given(chatRoomMemberRepository.findPartnerIdByRequesterId(chatRoomId, requesterId)).willReturn(Optional.of(partnerId));

    // when
    UUID result = chatRoomMemberReader.readPartnerIdByRequesterId(chatRoomId, requesterId);

    // then
    assertThat(result).isEqualTo(partnerId);
    then(chatRoomMemberRepository).should().findPartnerIdByRequesterId(chatRoomId, requesterId);
  }

  @Test
  @DisplayName("상대방 ID 조회 - 실패 테스트 (존재하지 않음)")
  void 상대방이_없으면_예외를_던진다() {
    // given
    Long chatRoomId = 1L;
    UUID requesterId = MEMBER_ID;

    given(chatRoomMemberRepository.findPartnerIdByRequesterId(chatRoomId, requesterId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
        chatRoomMemberReader.readPartnerIdByRequesterId(chatRoomId, requesterId)
    )
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_EXISTS_CHAT_PARTNER.getMessage());

    then(chatRoomMemberRepository).should().findPartnerIdByRequesterId(chatRoomId, requesterId);
  }

  @Test
  @DisplayName("채팅방 멤버 ID 목록 조회 테스트")
  void 채팅방의_모든_멤버_ID를_정상적으로_조회할_수_있다() {
    // given
    Long chatRoomId = 1L;

    given(chatRoomMemberRepository.findByChatRoomId(chatRoomId)).willReturn(List.of(CHAT_ROOM_MEMBER_1(), CHAT_ROOM_MEMBER_2()));

    // when
    List<UUID> result = chatRoomMemberReader.readChatRoomMemberIds(chatRoomId);

    // then
    assertThat(result).containsExactlyInAnyOrder(MEMBER_ID, OTHER_MEMBER_ID);
    then(chatRoomMemberRepository).should().findByChatRoomId(chatRoomId);
  }
}