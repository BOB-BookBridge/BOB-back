package com.bob.domain.chat.service;

import static com.bob.support.fixture.command.CreateChatRoomMembersCommandFixture.DEFAULT_CREATE_CHAT_ROOM_MEMBERS_COMMAND;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.chat.ChatRoomMemberFixture.CHAT_ROOM_MEMBER_1;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.chat.entity.ChatRoomMember;
import com.bob.domain.chat.repository.ChatRoomMemberRepository;
import com.bob.domain.chat.service.dto.command.CreateChatRoomMembersCommand;
import com.bob.domain.chat.service.dto.command.ExitChatRoomCommand;
import com.bob.domain.chat.service.reader.ChatRoomMemberReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("채팅방 회원 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class ChatRoomMemberServiceTest {

  @InjectMocks
  private ChatRoomMemberService chatRoomMemberService;

  @Mock
  private ChatRoomMemberRepository chatRoomMemberRepository;

  @Mock
  private ChatRoomMemberReader chatRoomMemberReader;

  @Test
  @DisplayName("채팅방 회원 등록 테스트")
  void 채팅방에_회원을_등록할_수_있다() {
    // given
    CreateChatRoomMembersCommand command = DEFAULT_CREATE_CHAT_ROOM_MEMBERS_COMMAND();
    ArgumentCaptor<List<ChatRoomMember>> captor = ArgumentCaptor.forClass(List.class);

    // when
    chatRoomMemberService.registerChatRoomMembersProcess(command);

    // then
    then(chatRoomMemberRepository).should(times(1)).saveAll(captor.capture());

    List<ChatRoomMember> savedMembers = captor.getValue();
    assertThat(savedMembers).hasSize(2);
    assertThat(savedMembers).allSatisfy(member ->
        assertThat(command.memberIds()).contains(member.getMemberId())
    );
  }

  @Test
  @DisplayName("채팅방 나가기 테스트")
  void 채팅방을_나가면_exitedAt이_현재_시간으로_설정된다() {
    // given
    ExitChatRoomCommand command = ExitChatRoomCommand.of(1L, MEMBER_ID);
    ChatRoomMember member = CHAT_ROOM_MEMBER_1();

    given(chatRoomMemberReader.readChatRoomMember(anyLong(), any(UUID.class))).willReturn(member);

    // when
    chatRoomMemberService.exitChatRoomMemberProcess(command);

    // then
    assertThat(member.getExitedAt()).isNotNull();
    assertThat(member.getExitedAt()).isCloseTo(LocalDateTime.now(), within(1, SECONDS));
    then(chatRoomMemberReader).should(times(1)).readChatRoomMember(1L, MEMBER_ID);
  }
}
