package com.bob.domain.chat.service;

import static com.bob.support.fixture.command.CreateChatRoomCommandFixture.DEFAULT_CREATE_CHAT_ROOM_COMMAND;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.response.ChatPostResponseFixture.DEFAULT_CHAT_POST_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;

import com.bob.domain.chat.entity.ChatRoom;
import com.bob.domain.chat.repository.ChatRoomRepository;
import com.bob.domain.chat.service.dto.command.CreateChatRoomCommand;
import com.bob.domain.chat.service.dto.command.CreateChatRoomMembersCommand;
import com.bob.domain.chat.service.dto.response.ChatPostResponse;
import com.bob.domain.chat.service.dto.response.CreateChatRoomResponse;
import com.bob.domain.chat.service.port.out.ChatPostPort;
import com.bob.domain.chat.service.port.out.ChatTradePort;
import com.bob.domain.chat.service.reader.ChatRoomReader;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("채팅방 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

  @InjectMocks
  private ChatRoomService chatRoomService;

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Mock
  private ChatRoomReader chatRoomReader;

  @Mock
  private ChatRoomMemberService chatRoomMemberService;

  @Mock
  private ChatPostPort postPort;

  @Mock
  private ChatTradePort tradePort;

  @Test
  @DisplayName("채팅방 생성 - 성공 테스트")
  void 채팅방을_생성할_수_있다() {
    // given
    CreateChatRoomCommand command = DEFAULT_CREATE_CHAT_ROOM_COMMAND(OTHER_MEMBER_ID); // 게시글 작성자는 기본 MEMBER_ID
    ChatPostResponse post = DEFAULT_CHAT_POST_RESPONSE();

    given(postPort.readChatPostSummary(command.postId())).willReturn(post);
    given(chatRoomReader.readExistingChatRoom(post.postId(), post.sellerId(), command.buyerId())).willReturn(Optional.empty());
    given(tradePort.createTrade(post.postId(), post.sellerId(), command.buyerId())).willReturn(1L);
    given(chatRoomRepository.save(any(ChatRoom.class)))
        .willAnswer(invocation -> {
          ChatRoom chatRoom = invocation.getArgument(0);
          ReflectionTestUtils.setField(chatRoom, "id", 1L);
          return chatRoom;
        });

    // when
    CreateChatRoomResponse response = chatRoomService.createChatRoomProcess(command);

    // then
    assertThat(response.chatRoomId()).isEqualTo(1L);
    then(chatRoomMemberService).should().registerChatRoomMembers(
        CreateChatRoomMembersCommand.of(1L, List.of(post.sellerId(), command.buyerId()))
    );
  }

  @Test
  @DisplayName("채팅방 생성 - 이미 존재하는 채팅방 테스트")
  void 기존_채팅방이_존재하면_해당_ID를_반환한다() {
    // given
    CreateChatRoomCommand command = DEFAULT_CREATE_CHAT_ROOM_COMMAND(OTHER_MEMBER_ID);
    ChatPostResponse post = DEFAULT_CHAT_POST_RESPONSE();
    Long existingRoomId = 1L;

    given(postPort.readChatPostSummary(command.postId())).willReturn(post);
    given(chatRoomReader.readExistingChatRoom(post.postId(), post.sellerId(), command.buyerId())).willReturn(
        Optional.of(existingRoomId));

    // when
    CreateChatRoomResponse result = chatRoomService.createChatRoomProcess(command);

    // then
    assertThat(result.chatRoomId()).isEqualTo(existingRoomId);
    then(chatRoomMemberService).should(never()).registerChatRoomMembers(any());
    then(chatRoomRepository).shouldHaveNoInteractions();
    then(tradePort).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("채팅방 생성 - 실패 테스트 (본인 게시글 채팅방 생성 요청)")
  void 본인_게시글에는_채팅방을_생성할_수_없다() {
    // given
    CreateChatRoomCommand command = DEFAULT_CREATE_CHAT_ROOM_COMMAND(MEMBER_ID);
    ChatPostResponse post = ChatPostResponse.of(
        command.postId(),
        command.buyerId(), // seller == buyer
        "제목",
        "image.png"
    );

    given(postPort.readChatPostSummary(command.postId())).willReturn(post);

    // when & then
    assertThatThrownBy(() -> chatRoomService.createChatRoomProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.IS_SAME_CHAT_MEMBER.getMessage());

    then(chatRoomReader).shouldHaveNoInteractions();
    then(tradePort).shouldHaveNoInteractions();
    then(chatRoomRepository).shouldHaveNoInteractions();
    then(chatRoomMemberService).shouldHaveNoInteractions();
  }
}
