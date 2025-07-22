package com.bob.domain.chat.service;

import static com.bob.domain.chat.entity.type.ChatMessageType.SYSTEM;
import static com.bob.domain.chat.service.dto.command.CreateChatMessageCommand.IS_FAR_MEMBER;
import static com.bob.global.exception.response.ApplicationError.IS_SAME_CHAT_MEMBER;
import static com.bob.global.exception.response.ApplicationError.NOT_EXISTS_CHAT_PARTNER;
import static com.bob.support.fixture.command.CreateChatMessageCommandFixture.CUSTOM_CREATE_CHAT_MESSAGE_COMMAND;
import static com.bob.support.fixture.command.CreateChatMessageCommandFixture.DEFAULT_CREATE_CHAT_MESSAGE_COMMAND;
import static com.bob.support.fixture.command.CreateChatRoomCommandFixture.DEFAULT_CREATE_CHAT_ROOM_COMMAND;
import static com.bob.support.fixture.command.CreateChatRoomCommandFixture.DEFAULT_CREATE_CHAT_ROOM_COMMAND_WITH_FAR;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.domain.chat.ChatMessageFixture.DEFAULT_TEXT_CHAT_MESSAGE;
import static com.bob.support.fixture.domain.chat.ChatMessageFixture.WITH_IMAGE_CHAT_MESSAGE;
import static com.bob.support.fixture.domain.chat.ChatRoomFixture.DEFAULT_CHAT_ROOM_1;
import static com.bob.support.fixture.domain.chat.ChatRoomFixture.DISABLE_CHAT_ROOM_1;
import static com.bob.support.fixture.domain.chat.ChatRoomFixture.customChatRoom;
import static com.bob.support.fixture.domain.chat.ChatRoomMemberFixture.CHAT_ROOM_MEMBER_1;
import static com.bob.support.fixture.response.ChatFileSummaryResponseFixture.DEFAULT_READ_FILES_RESPONSE;
import static com.bob.support.fixture.response.ChatPostResponseFixture.DEFAULT_CHAT_POST_RESPONSE;
import static com.bob.support.fixture.response.MemberProfileResponseFixture.OTHER_MEMBER_PROFILE_RESPONSE;
import static com.bob.support.fixture.response.PostResponseFixture.DEFAULT_POST_DETAIL_RESPONSE;
import static com.bob.support.fixture.response.TradeDetailResponseFixture.DEFAULT_TRADE_DETAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bob.domain.chat.entity.ChatMessage;
import com.bob.domain.chat.entity.ChatRoom;
import com.bob.domain.chat.entity.ChatRoomMember;
import com.bob.domain.chat.repository.ChatMessageRepository;
import com.bob.domain.chat.repository.ChatRoomRepository;
import com.bob.domain.chat.service.dto.command.CreateChatMessageCommand;
import com.bob.domain.chat.service.dto.command.CreateChatRoomCommand;
import com.bob.domain.chat.service.dto.command.CreateChatRoomMembersCommand;
import com.bob.domain.chat.service.dto.command.EnterChatRoomCommand;
import com.bob.domain.chat.service.dto.command.ExitChatRoomCommand;
import com.bob.domain.chat.service.dto.command.ReEnterChatRoomCommand;
import com.bob.domain.chat.service.dto.query.ReadChatMessagesQuery;
import com.bob.domain.chat.service.dto.query.ReadChatRoomDetailQuery;
import com.bob.domain.chat.service.dto.query.ReadChatRoomListQuery;
import com.bob.domain.chat.service.dto.query.ReadUnreadMessageCountQuery;
import com.bob.domain.chat.service.dto.response.ChatMessagesResponse;
import com.bob.domain.chat.service.dto.response.ChatPostResponse;
import com.bob.domain.chat.service.dto.response.ChatRoomDetailResponse;
import com.bob.domain.chat.service.dto.response.ChatRoomSummaryResponse;
import com.bob.domain.chat.service.dto.response.ChatTradeResponse;
import com.bob.domain.chat.service.dto.response.CreateChatRoomResponse;
import com.bob.domain.chat.service.port.out.ChatFilePort;
import com.bob.domain.chat.service.port.out.ChatMemberPort;
import com.bob.domain.chat.service.port.out.ChatPostPort;
import com.bob.domain.chat.service.port.out.ChatTradePort;
import com.bob.domain.chat.service.reader.ChatMessageReader;
import com.bob.domain.chat.service.reader.ChatRoomMemberReader;
import com.bob.domain.chat.service.reader.ChatRoomReader;
import com.bob.global.event.application.dto.NotiEvent;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
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
  private ChatRoomMemberReader chatRoomMemberReader;

  @Mock
  private ChatMessageRepository chatMessageRepository;

  @Mock
  private ChatMessageReader chatMessageReader;

  @Mock
  private ChatRoomMemberService chatRoomMemberService;

  @Mock
  private ChatMessageService chatMessageService;

  @Mock
  private ChatPostPort postPort;

  @Mock
  private ChatFilePort filePort;

  @Mock
  private ChatTradePort tradePort;

  @Mock
  private ChatMemberPort memberPort;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Test
  @DisplayName("채팅방 생성 - 성공 테스트")
  void 채팅방을_생성할_수_있다() {
    // given
    CreateChatRoomCommand command = DEFAULT_CREATE_CHAT_ROOM_COMMAND(OTHER_MEMBER_ID); // 게시글 작성자는 기본 MEMBER_ID
    ChatPostResponse post = DEFAULT_CHAT_POST_RESPONSE;

    given(postPort.readChatPostSummary(command.postId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(post.postId()));
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
    then(chatRoomMemberService).should().registerChatRoomMembersProcess(
        CreateChatRoomMembersCommand.of(1L, List.of(post.sellerId(), command.buyerId()))
    );
  }

  @DisplayName("채팅방 생성 - 거리가 먼 사용자 SYSTEM 메시지 등록 테스트")
  @Test
  void isFar_사용자가_채팅방을_생성하면_SYSTEM_메시지가_자동_저장된다() {
    // given
    CreateChatRoomCommand command = DEFAULT_CREATE_CHAT_ROOM_COMMAND_WITH_FAR(OTHER_MEMBER_ID);
    ChatPostResponse post = DEFAULT_CHAT_POST_RESPONSE;

    given(postPort.readChatPostSummary(command.postId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(post.postId()));
    given(chatRoomReader.readExistingChatRoom(post.postId(), post.sellerId(), command.buyerId())).willReturn(Optional.empty());
    given(tradePort.createTrade(post.postId(), post.sellerId(), command.buyerId())).willReturn(1L);
    given(chatRoomRepository.save(any(ChatRoom.class))).willAnswer(invocation -> {
      ChatRoom chatRoom = invocation.getArgument(0);
      ReflectionTestUtils.setField(chatRoom, "id", 1L);
      return chatRoom;
    });

    // when
    chatRoomService.createChatRoomProcess(command);

    // then
    then(chatMessageService).should(times(1)).createSystemChatMessageProcess(any());
  }

  @Test
  @DisplayName("채팅방 생성 - 이미 존재하는 채팅방 테스트")
  void 기존_채팅방이_존재하면_해당_ID를_반환한다() {
    // given
    CreateChatRoomCommand command = DEFAULT_CREATE_CHAT_ROOM_COMMAND(OTHER_MEMBER_ID);
    ChatPostResponse post = DEFAULT_CHAT_POST_RESPONSE;
    Long existingRoomId = 1L;

    given(postPort.readChatPostSummary(command.postId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(post.postId()));
    given(chatRoomReader.readExistingChatRoom(post.postId(), post.sellerId(), command.buyerId())).willReturn(Optional.of(existingRoomId));

    // when
    CreateChatRoomResponse result = chatRoomService.createChatRoomProcess(command);

    // then
    assertThat(result.chatRoomId()).isEqualTo(existingRoomId);
    then(chatRoomMemberService).should(never()).registerChatRoomMembersProcess(any());
    then(chatRoomRepository).shouldHaveNoInteractions();
    then(tradePort).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("채팅방 생성 - 재입장 테스트")
  void 기존_채팅방이_존재하고_재입장하는_경우_채팅방_ID를_반환하고_재입장_처리한다() {
    // given
    CreateChatRoomCommand command = DEFAULT_CREATE_CHAT_ROOM_COMMAND(OTHER_MEMBER_ID);
    ChatPostResponse post = DEFAULT_CHAT_POST_RESPONSE;
    Long chatRoomId = 1L;

    given(postPort.readChatPostSummary(command.postId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(post.postId()));
    given(chatRoomReader.readExistingChatRoom(post.postId(), post.sellerId(), command.buyerId())).willReturn(Optional.of(chatRoomId));

    // when
    CreateChatRoomResponse result = chatRoomService.createChatRoomProcess(command);

    // then
    assertThat(result.chatRoomId()).isEqualTo(chatRoomId);
    then(chatRoomMemberService).should(never()).registerChatRoomMembersProcess(any());
    then(chatRoomRepository).shouldHaveNoInteractions();
    then(tradePort).shouldHaveNoInteractions();
    then(chatRoomMemberService).should().reEnterChatRoomMembersProcess(ReEnterChatRoomCommand.of(chatRoomId, OTHER_MEMBER_ID));
  }

  @Test
  @DisplayName("채팅방 생성 - 실패 테스트 (본인 게시글 채팅방 생성 요청)")
  void 본인_게시글에는_채팅방을_생성할_수_없다() {
    // given
    CreateChatRoomCommand command = DEFAULT_CREATE_CHAT_ROOM_COMMAND(MEMBER_ID);
    ChatPostResponse post = DEFAULT_CHAT_POST_RESPONSE;

    given(postPort.readChatPostSummary(command.postId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(post.postId()));

    // when & then
    assertThatThrownBy(() -> chatRoomService.createChatRoomProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(IS_SAME_CHAT_MEMBER.getMessage());

    then(chatRoomReader).shouldHaveNoInteractions();
    then(tradePort).shouldHaveNoInteractions();
    then(chatRoomRepository).shouldHaveNoInteractions();
    then(chatRoomMemberService).shouldHaveNoInteractions();
  }

  @DisplayName("채팅 메시지 전송 - 성공 테스트")
  @Test
  void 채팅방_참여자라면_채팅_메시지를_전송할_수_있다() {
    // given
    Long chatRoomId = 1L;
    UUID senderId = MEMBER_ID;
    UUID receiverId = OTHER_MEMBER_ID;
    CreateChatMessageCommand command = DEFAULT_CREATE_CHAT_MESSAGE_COMMAND();
    given(chatRoomReader.readChatRoomById(chatRoomId)).willReturn(DEFAULT_CHAT_ROOM_1());
    given(chatRoomMemberReader.readChatRoomMember(chatRoomId, MEMBER_ID)).willReturn(CHAT_ROOM_MEMBER_1());
    given(chatRoomMemberReader.readPartnerIdByRequesterId(chatRoomId, senderId)).willReturn(receiverId);
    given(chatMessageService.createChatMessageProcess(command, receiverId)).willReturn(DEFAULT_TEXT_CHAT_MESSAGE());

    // when
    chatRoomService.createChatRoomMessageProcess(command);

    // then
    then(chatMessageService).should(times(1)).createChatMessageProcess(command, receiverId);
    then(chatRoomMemberReader).should(times(1)).readChatRoomMember(chatRoomId, MEMBER_ID);
    then(chatRoomMemberReader).should(times(1)).readPartnerIdByRequesterId(chatRoomId, senderId);
    then(eventPublisher).should(times(1)).publishEvent(any(NotiEvent.class));
  }

  @DisplayName("채팅 메시지 전송 - 메시지 타입이 IMAGE, MIX인 경우 normalize = true 테스트")
  @Test
  void 채팅_메시지_타입이_IMAGE또는_MIX이면_normalize_는_true인_알림이_전송된다() {
    // given
    Long chatRoomId = 1L;
    UUID memberId = MEMBER_ID;
    List<UUID> members = List.of(memberId, OTHER_MEMBER_ID);

    CreateChatMessageCommand command = new CreateChatMessageCommand(chatRoomId, memberId, "", List.of("image.jpg"));
    ChatMessage chatMessage = WITH_IMAGE_CHAT_MESSAGE();

    given(chatRoomReader.readChatRoomById(chatRoomId)).willReturn(DEFAULT_CHAT_ROOM_1());
    given(chatRoomMemberReader.readChatRoomMember(chatRoomId, MEMBER_ID)).willReturn(CHAT_ROOM_MEMBER_1());
    given(chatMessageService.createChatMessageProcess(command, OTHER_MEMBER_ID)).willReturn(chatMessage);
    given(chatRoomMemberReader.readPartnerIdByRequesterId(chatRoomId, memberId)).willReturn(OTHER_MEMBER_ID);

    // when
    chatRoomService.createChatRoomMessageProcess(command);

    // then
    ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
    verify(eventPublisher).publishEvent(eventCaptor.capture());
    Object publishedEvent = eventCaptor.getValue();
    assertThat(publishedEvent).isInstanceOf(NotiEvent.class);
    NotiEvent notiEvent = (NotiEvent) publishedEvent;
    assertThat(notiEvent.normalize()).isTrue();
  }

  @DisplayName("채팅 메시지 전송 - 비활성화 채팅방 활성화 테스트")
  @Test
  void 비활성화된_채팅방에_첫_메시지를_보내면_채팅방이_활성화된다() {
    // given
    Long chatRoomId = 1L;
    UUID senderId = MEMBER_ID;
    UUID receiverId = OTHER_MEMBER_ID;
    CreateChatMessageCommand command = DEFAULT_CREATE_CHAT_MESSAGE_COMMAND();
    ChatRoom disabledChatRoom = DISABLE_CHAT_ROOM_1();

    given(chatRoomReader.readChatRoomById(chatRoomId)).willReturn(disabledChatRoom);
    given(chatRoomMemberReader.readChatRoomMember(chatRoomId, MEMBER_ID)).willReturn(CHAT_ROOM_MEMBER_1());
    given(chatRoomMemberReader.readPartnerIdByRequesterId(chatRoomId, senderId)).willReturn(receiverId);
    given(chatMessageService.createChatMessageProcess(command, receiverId)).willReturn(DEFAULT_TEXT_CHAT_MESSAGE());

    // when
    chatRoomService.createChatRoomMessageProcess(command);

    // then
    assertThat(disabledChatRoom.getEnableStatus()).isTrue();
    then(chatMessageService).should().createChatMessageProcess(command, receiverId);
    then(eventPublisher).should().publishEvent(any(NotiEvent.class));
  }

  @DisplayName("채팅 메시지 전송 - 실패 테스트(채팅방에 참여하지 않은 경우)")
  @Test
  void 채팅방에_참여하지_않은_회원이_메시지를_보내면_예외가_발생한다() {
    // given
    Long chatRoomId = 1L;
    UUID unknown = UUID.randomUUID();
    CreateChatMessageCommand command = CUSTOM_CREATE_CHAT_MESSAGE_COMMAND(unknown);
    given(chatRoomMemberReader.readChatRoomMember(chatRoomId, unknown))
        .willThrow(new ApplicationException(ApplicationError.NOT_PARTICIPATED_CHAT_ROOM));

    // when & then
    assertThatThrownBy(() -> chatRoomService.createChatRoomMessageProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_PARTICIPATED_CHAT_ROOM.getMessage());

    then(chatMessageService).shouldHaveNoInteractions();
    then(eventPublisher).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("활성화 된 채팅방 및 상대방 정보 존재가 존재하는 경우 반환 목록에 포함 테스트")
  void 유효한_채팅방_정보를_정상적으로_반환한다() {
    // given
    ChatRoom chatRoom = DEFAULT_CHAT_ROOM_1();
    UUID memberId = MEMBER_ID;
    UUID partnerId = OTHER_MEMBER_ID;

    given(chatRoomReader.readParticipatingChatRoomsByMemberId(memberId)).willReturn(List.of(chatRoom));
    given(chatRoomMemberReader.readPartnerIdByRequesterId(chatRoom.getId(), memberId)).willReturn(partnerId);
    given(memberPort.readChatMemberProfile(OTHER_MEMBER_ID)).willReturn(OTHER_MEMBER_PROFILE_RESPONSE);
    given(postPort.readChatPostSummary(chatRoom.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(chatRoom.getPostId()));
    given(chatMessageReader.readUnreadMessageCountOfChatRoom(chatRoom.getId(), memberId)).willReturn(2);

    // when
    List<ChatRoomSummaryResponse> result = chatRoomService.readChatRoomListProcess(ReadChatRoomListQuery.of(memberId));

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).chatroomId()).isEqualTo(chatRoom.getId());
    assertThat(result.get(0).unreadCount()).isEqualTo(2);
  }

  @Test
  @DisplayName("채팅방 마지막 메시지 시간에 따른 정렬 테스트")
  void 채팅방이_여러_개일_때_마지막_메시지_시간_기준으로_내림차순_정렬된다() {
    // given
    UUID memberId = MEMBER_ID;
    UUID partnerId = OTHER_MEMBER_ID;

    ChatRoom recentRoom = customChatRoom(1L, "최신 메시지", LocalDateTime.now(), true);
    ChatRoom oldRoom = customChatRoom(2L, "오래된 메시지", LocalDateTime.now().minusHours(1), true);

    given(chatRoomReader.readParticipatingChatRoomsByMemberId(memberId)).willReturn(List.of(oldRoom, recentRoom));
    given(chatRoomMemberReader.readPartnerIdByRequesterId(anyLong(), eq(memberId))).willReturn(partnerId);
    given(memberPort.readChatMemberProfile(partnerId)).willReturn(OTHER_MEMBER_PROFILE_RESPONSE);
    given(postPort.readChatPostSummary(anyLong())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));
    given(chatMessageReader.readUnreadMessageCountOfChatRoom(anyLong(), eq(memberId))).willReturn(0);

    // when
    List<ChatRoomSummaryResponse> result = chatRoomService.readChatRoomListProcess(ReadChatRoomListQuery.of(memberId));

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).chatroomId()).isEqualTo(recentRoom.getId());
    assertThat(result.get(1).chatroomId()).isEqualTo(oldRoom.getId());
  }

  @Test
  @DisplayName("활성화 되지 않은 채팅방인 경우 반환 목록 제외 테스트")
  void enableStatus가_false면_목록에서_제외된다() {
    // given
    UUID memberId = MEMBER_ID;
    given(chatRoomReader.readParticipatingChatRoomsByMemberId(memberId)).willReturn(List.of(DISABLE_CHAT_ROOM_1()));

    // when
    List<ChatRoomSummaryResponse> result = chatRoomService.readChatRoomListProcess(ReadChatRoomListQuery.of(memberId));

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("상대방 정보 조회 실패 시 해당 채팅방은 제외된다")
  void 상대방_정보를_조회할_수_없으면_제외된다() {
    // given
    ChatRoom chatRoom = DEFAULT_CHAT_ROOM_1();
    UUID memberId = MEMBER_ID;

    given(chatRoomReader.readParticipatingChatRoomsByMemberId(memberId)).willReturn(List.of(chatRoom));
    given(chatRoomMemberReader.readPartnerIdByRequesterId(chatRoom.getId(), memberId))
        .willThrow(new ApplicationException(NOT_EXISTS_CHAT_PARTNER));

    // when
    List<ChatRoomSummaryResponse> result = chatRoomService.readChatRoomListProcess(ReadChatRoomListQuery.of(memberId));

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("읽지 않은 전체 메시지 개수 조회 - 성공 테스트")
  void 참여_중인_채팅방이_여러_개일_때_읽지_않은_메시지_수를_정상적으로_합산한다() {
    // given
    UUID memberId = MEMBER_ID;
    ChatRoom room1 = customChatRoom(1L, "message", LocalDateTime.now(), true);
    ChatRoom room2 = customChatRoom(2L, "message", LocalDateTime.now().minusMinutes(10), true);

    given(chatRoomReader.readParticipatingChatRoomsByMemberId(memberId))
        .willReturn(List.of(room1, room2));
    given(chatMessageReader.readUnreadMessageCountOfChatRoom(room1.getId(), memberId)).willReturn(3);
    given(chatMessageReader.readUnreadMessageCountOfChatRoom(room2.getId(), memberId)).willReturn(2);

    // when
    int result = chatRoomService.countUnreadMessageProcess(ReadUnreadMessageCountQuery.of(memberId));

    // then
    assertThat(result).isEqualTo(5);
    then(chatRoomReader).should().readParticipatingChatRoomsByMemberId(memberId);
    then(chatMessageReader).should().readUnreadMessageCountOfChatRoom(room1.getId(), memberId);
    then(chatMessageReader).should().readUnreadMessageCountOfChatRoom(room2.getId(), memberId);
  }

  @DisplayName("채팅방 상세 조회 - 성공 테스트")
  @Test
  void 채팅방_상세정보를_정상적으로_조회할_수_있다() {
    // given
    Long chatRoomId = 1L;
    UUID memberId = MEMBER_ID;
    UUID partnerId = OTHER_MEMBER_ID;

    ChatRoom chatRoom = customChatRoom(1L, "lastMessage", LocalDateTime.now(), true);
    ChatPostResponse postResponse = ChatPostResponse.from(DEFAULT_POST_DETAIL_RESPONSE(chatRoom.getPostId()));
    ChatTradeResponse tradeResponse = ChatTradeResponse.from(DEFAULT_TRADE_DETAIL());

    given(chatRoomReader.readChatRoomById(chatRoomId)).willReturn(chatRoom);
    given(chatRoomMemberReader.readChatRoomMember(chatRoomId, MEMBER_ID)).willReturn(CHAT_ROOM_MEMBER_1());
    given(chatRoomMemberReader.readPartnerIdByRequesterId(chatRoomId, memberId)).willReturn(partnerId);
    given(postPort.readChatPostSummary(chatRoom.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(chatRoom.getPostId()));
    given(tradePort.readChatTradeSummary(chatRoomId, memberId)).willReturn(DEFAULT_TRADE_DETAIL());
    given(memberPort.readChatMemberProfile(partnerId)).willReturn(OTHER_MEMBER_PROFILE_RESPONSE);
    ReadChatRoomDetailQuery query = ReadChatRoomDetailQuery.of(chatRoomId, memberId);

    // when
    ChatRoomDetailResponse response = chatRoomService.readChatRoomDetailProcess(query);

    // then
    assertThat(response.chatroomId()).isEqualTo(chatRoomId);
    assertThat(response.partner().id()).isEqualTo(partnerId);
    assertThat(response.post().id()).isEqualTo(postResponse.postId());
    assertThat(response.trade().status()).isEqualTo(tradeResponse.status());

    then(chatRoomReader).should().readChatRoomById(chatRoomId);
    then(chatRoomMemberReader).should().readChatRoomMember(chatRoomId, MEMBER_ID);
    then(chatRoomMemberReader).should().readPartnerIdByRequesterId(chatRoomId, memberId);
    then(postPort).should().readChatPostSummary(chatRoom.getPostId());
    then(tradePort).should().readChatTradeSummary(chatRoomId, memberId);
    then(memberPort).should().readChatMemberProfile(partnerId);
  }

  @Test
  @DisplayName("채팅방 상세 조회 - 채팅방에 속하지 않은 경우 예외가 발생한다")
  void 채팅방에_속하지_않은_회원은_예외가_발생한다() {
    // given
    Long chatRoomId = 1L;
    ChatRoom chatRoom = DEFAULT_CHAT_ROOM_1();

    given(chatRoomReader.readChatRoomById(chatRoomId)).willReturn(chatRoom);
    given(chatRoomMemberReader.readChatRoomMember(chatRoomId, MEMBER_ID))
        .willThrow(new ApplicationException(ApplicationError.NOT_PARTICIPATED_CHAT_ROOM));

    ReadChatRoomDetailQuery query = ReadChatRoomDetailQuery.of(chatRoomId, MEMBER_ID);

    // when & then
    assertThatThrownBy(() -> chatRoomService.readChatRoomDetailProcess(query))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_PARTICIPATED_CHAT_ROOM.getMessage());

    then(chatRoomReader).should().readChatRoomById(chatRoomId);
    then(chatRoomMemberReader).should().readChatRoomMember(chatRoomId, MEMBER_ID);
    then(chatRoomMemberReader).shouldHaveNoMoreInteractions();
    then(postPort).shouldHaveNoInteractions();
    then(tradePort).shouldHaveNoInteractions();
    then(memberPort).shouldHaveNoInteractions();
  }

  @DisplayName("채팅 내역 조회 - 성공 테스트")
  @Test
  void 채팅_내역을_정상적으로_조회할_수_있다() {
    // given
    Long chatRoomId = 1L;
    UUID memberId = MEMBER_ID;
    ChatRoomMember member = CHAT_ROOM_MEMBER_1();
    int size = 2; // hasNext 테스트 용 size 정의

    ChatMessage message1 = DEFAULT_TEXT_CHAT_MESSAGE();
    ChatMessage message2 = WITH_IMAGE_CHAT_MESSAGE();

    given(chatRoomMemberReader.readChatRoomMember(chatRoomId, memberId)).willReturn(member);
    given(filePort.readChatFileSummaries(chatRoomId)).willReturn(DEFAULT_READ_FILES_RESPONSE);
    given(chatMessageReader.readRecentMessages(chatRoomId, member.getEnteredAt(), size)).willReturn(List.of(message1, message2));

    // when
    ChatMessagesResponse response = chatRoomService.readChatMessagesProcess(ReadChatMessagesQuery.of(memberId, chatRoomId, null, size));

    // then
    assertThat(response.messages()).hasSize(2);
    assertThat(response.hasNext()).isTrue();
    assertThat(response.messages().get(0).id()).isEqualTo(message1.getId());
    assertThat(response.messages().get(0).isMine()).isTrue();
  }

  @DisplayName("채팅 내역 조회 - 실패 테스트 (채팅방 미참여)")
  @Test
  void 채팅방에_참여하지_않은_회원이면_예외가_발생한다() {
    // given
    Long chatRoomId = 1L;
    UUID memberId = MEMBER_ID;

    ChatRoomMember exitedMember = CHAT_ROOM_MEMBER_1();
    exitedMember.updateExitedAt(LocalDateTime.now());

    given(chatRoomMemberReader.readChatRoomMember(chatRoomId, memberId)).willReturn(exitedMember);

    // when & then
    assertThatThrownBy(() -> chatRoomService.readChatMessagesProcess(
        ReadChatMessagesQuery.of(memberId, chatRoomId, null, 20)))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_PARTICIPATED_CHAT_ROOM.getMessage());
  }

  @Test
  @DisplayName("채팅방 나기기 - 성공 테스트")
  void 채팅방을_나가면_상태를_업데이트하고_채팅들을_읽음_처리한다() {
    // given
    Long chatRoomId = 1L;
    ExitChatRoomCommand command = ExitChatRoomCommand.of(chatRoomId, MEMBER_ID);

    // when
    chatRoomService.exitChatRoomProcess(command);

    // then
    then(chatRoomMemberService).should(times(1)).exitChatRoomMemberProcess(command);
    then(chatMessageService).should(times(1)).updateReadStatusProcess(EnterChatRoomCommand.of(command.chatRoomId(), command.memberId()));
  }

  @Test
  @DisplayName("채팅방 입장 - 메시지 읽음 처리 테스트")
  void 채팅방_입장_시_해당_채팅방의_안_읽은_메시지를_읽음_처리한다() {
    // given
    Long chatRoomId = 1L;
    EnterChatRoomCommand command = EnterChatRoomCommand.of(chatRoomId, MEMBER_ID);

    // when
    chatRoomService.enterChatRoomProcess(command);

    // then
    then(chatMessageService).should(times(1)).updateReadStatusProcess(command);
  }
}
