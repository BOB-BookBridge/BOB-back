package com.bob.domain.chat.service;

import static com.bob.domain.chat.entity.type.ChatMessageType.SYSTEM;
import static com.bob.domain.chat.service.dto.command.CreateChatMessageCommand.IS_FAR_MEMBER;
import static com.bob.support.fixture.command.CreateChatMessageCommandFixture.CUSTOM_WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND;
import static com.bob.support.fixture.command.CreateChatRoomCommandFixture.of;
import static com.bob.support.fixture.domain.MemberFixture.otherMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bob.domain.chat.entity.ChatMessage;
import com.bob.domain.chat.entity.ChatRoom;
import com.bob.domain.chat.entity.ChatRoomMember;
import com.bob.domain.chat.repository.ChatMessageRepository;
import com.bob.domain.chat.repository.ChatRoomMemberRepository;
import com.bob.domain.chat.repository.ChatRoomRepository;
import com.bob.domain.chat.service.dto.command.CreateChatMessageCommand;
import com.bob.domain.chat.service.dto.command.CreateChatRoomCommand;
import com.bob.domain.chat.service.dto.command.EnterChatRoomCommand;
import com.bob.domain.chat.service.dto.query.ReadChatMessagesQuery;
import com.bob.domain.chat.service.dto.query.ReadChatRoomDetailQuery;
import com.bob.domain.chat.service.dto.query.ReadChatRoomListQuery;
import com.bob.domain.chat.service.dto.query.ReadUnreadMessageCountQuery;
import com.bob.domain.chat.service.dto.response.ChatMessageSendResponse;
import com.bob.domain.chat.service.dto.response.ChatMessagesResponse;
import com.bob.domain.chat.service.dto.response.ChatRoomDetailResponse;
import com.bob.domain.chat.service.dto.response.ChatRoomSummaryResponse;
import com.bob.domain.chat.service.dto.response.CreateChatRoomResponse;
import com.bob.domain.member.entity.Member;
import com.bob.domain.member.repository.MemberRepository;
import com.bob.domain.post.entity.Post;
import com.bob.domain.post.repository.PostRepository;
import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.repository.TradeRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.support.TestContainerSupport;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("채팅방 서비스 통합 테스트")
@Transactional
@SpringBootTest
class ChatRoomServiceIntgTest extends TestContainerSupport {

  @Autowired
  private ChatRoomService chatRoomService;

  @Autowired
  private ChatRoomRepository chatRoomRepository;

  @Autowired
  private ChatRoomMemberRepository chatRoomMemberRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private TradeRepository tradeRepository;

  @Autowired
  private ChatMessageRepository chatMessageRepository;

  @Test
  void 채팅방_생성() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).get();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).get();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(0);
    Trade trade = createMockTrade(post.getId(), seller.getId(), buyer.getId());
    CreateChatRoomCommand command = of(post.getId(), trade.getId(), buyer.getId());

    // when
    CreateChatRoomResponse response = chatRoomService.createChatRoomProcess(command);

    // then
    ChatRoom chatRoom = chatRoomRepository.findById(response.chatRoomId()).orElseThrow();
    List<ChatRoomMember> members = chatRoomMemberRepository.findByChatRoomId(chatRoom.getId());

    assertThat(chatRoom.getPostId()).isEqualTo(post.getId());
    assertThat(chatRoom.getTitleSuffix()).contains("자바의 정석");
    assertThat(members).extracting(ChatRoomMember::getMemberId)
        .containsExactlyInAnyOrder(seller.getId(), buyer.getId());
  }

  @Test
  void 채팅방_생성_시_거리가_먼_사용자는_SYSTEM_메시지_등록() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).orElseThrow();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).orElseThrow();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(3);
    Trade trade = createMockTrade(post.getId(), seller.getId(), buyer.getId());
    CreateChatRoomCommand command = new CreateChatRoomCommand(post.getId(), trade.getId(), buyer.getId(), true);

    // when
    CreateChatRoomResponse response = chatRoomService.createChatRoomProcess(command);
    Long chatRoomId = response.chatRoomId();

    // then
    List<ChatMessage> messages = chatMessageRepository.findAllByChatRoomId(chatRoomId);
    assertThat(messages).hasSize(1);

    ChatMessage systemMessage = messages.get(0);
    assertThat(systemMessage.getType()).isEqualTo(SYSTEM);
    assertThat(systemMessage.getContent()).isEqualTo(IS_FAR_MEMBER);
    assertThat(systemMessage.getIsRead()).isTrue();
    assertThat(systemMessage.getSenderId()).isEqualTo(buyer.getId());
  }

  @Test
  void 채팅방_생성_시_이미_존재하는_채팅방이_있다면_기존_ID_반환() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).get();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).get();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(0);
    Trade trade = createMockTrade(post.getId(), seller.getId(), buyer.getId());
    CreateChatRoomCommand command = of(post.getId(), trade.getId(), buyer.getId()); // 최초 생성
    CreateChatRoomResponse created = chatRoomService.createChatRoomProcess(command);

    // when
    CreateChatRoomResponse result = chatRoomService.createChatRoomProcess(command);

    // then
    assertThat(result.chatRoomId()).isEqualTo(created.chatRoomId());
    assertThat(chatRoomRepository.findById(result.chatRoomId())).isNotNull();
    assertThat(chatRoomMemberRepository.findByChatRoomId(result.chatRoomId())).hasSize(2);
  }

  @Test
  void 채팅_메시지_전송() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).orElseThrow();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).orElseThrow();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(5);
    Trade trade = createMockTrade(post.getId(), seller.getId(), buyer.getId());
    CreateChatRoomResponse chatRoomResponse = chatRoomService.createChatRoomProcess(of(post.getId(), trade.getId(), buyer.getId()));
    Long chatRoomId = chatRoomResponse.chatRoomId();
    CreateChatMessageCommand command = CUSTOM_WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND(chatRoomId, buyer.getId());

    // when
    ChatMessageSendResponse response = chatRoomService.createChatRoomMessageProcess(command);

    // then
    List<ChatMessage> messages = chatMessageRepository.findAllByChatRoomId(chatRoomId);
    assertThat(messages).hasSize(1);

    ChatMessage message = messages.get(0);
    assertThat(message.getContent()).isEqualTo("message");
    assertThat(message.getType().name()).isEqualTo("MIX");
    assertThat(response.id()).isEqualTo(message.getId());
    assertThat(response.isRead()).isEqualTo(message.getIsRead());
    assertThat(response.sentAt()).isEqualTo(message.getCreatedAt());
  }

  @Test
  void 채팅_메시지_전송_시_상대방이_채팅방을_나간_경우_자동_재입장_처리() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).orElseThrow();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).orElseThrow();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(5);
    Trade trade = createMockTrade(post.getId(), seller.getId(), buyer.getId());
    CreateChatRoomResponse chatRoomResponse = chatRoomService.createChatRoomProcess(of(post.getId(), trade.getId(), buyer.getId()));
    Long chatRoomId = chatRoomResponse.chatRoomId();
    ChatRoomMember partner = chatRoomMemberRepository.findByChatRoomIdAndMemberId(chatRoomId, seller.getId()).get();
    partner.updateExitedAt(LocalDateTime.now()); // 상대방 채팅방 나가기
    CreateChatMessageCommand command = CUSTOM_WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND(chatRoomId, buyer.getId());

    // when
    chatRoomService.createChatRoomMessageProcess(command); // 상대방이 채팅방을 나간 경우 채팅방 재입장 처리

    // then
    assertThat(partner.getExitedAt()).isNull(); // 채팅방 입장 여부는 나간 시각의 null 여부로 판단 (null = 입장 상태)
  }

  @Test
  void 채팅방_목록_조회() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).get();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).get();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(0);
    Trade trade = createMockTrade(post.getId(), seller.getId(), buyer.getId());
    CreateChatRoomCommand command1 = of(post.getId(), trade.getId(), buyer.getId());
    CreateChatRoomResponse response1 = chatRoomService.createChatRoomProcess(command1);
    CreateChatRoomCommand command2 = of(post.getId(), trade.getId(), buyer.getId());
    chatRoomService.createChatRoomProcess(command2);
    ChatRoom enableChatRoom = chatRoomRepository.findById(response1.chatRoomId()).get();

    // when
    List<ChatRoomSummaryResponse> result = chatRoomService.readChatRoomListProcess(ReadChatRoomListQuery.of(buyer.getId()));

    // then
    assertThat(result).hasSize(1);
    ChatRoomSummaryResponse chatRoomSummary = result.get(0);
    assertThat(chatRoomSummary.chatroomId()).isEqualTo(enableChatRoom.getId());
    assertThat(chatRoomSummary.partner().id()).isEqualTo(seller.getId());
    assertThat(chatRoomSummary.thumbnailUrl()).isNotBlank();
    assertThat(chatRoomSummary.unreadCount()).isEqualTo(0);
  }

  @Test
  void 읽지_않은_전체_메시지_개수_조회() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).orElseThrow();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).orElseThrow();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(0);
    Trade trade = createMockTrade(post.getId(), seller.getId(), buyer.getId());
    Long chatRoomId1 = chatRoomService.createChatRoomProcess(of(post.getId(), trade.getId(), buyer.getId())).chatRoomId();
    chatRoomService.createChatRoomMessageProcess(CUSTOM_WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND(chatRoomId1, seller.getId()));
    Long chatRoomId2 = chatRoomService.createChatRoomProcess(of(post.getId(), trade.getId(), buyer.getId())).chatRoomId();
    chatRoomService.createChatRoomMessageProcess(CUSTOM_WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND(chatRoomId2, seller.getId()));
    chatRoomService.createChatRoomMessageProcess(CUSTOM_WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND(chatRoomId2, seller.getId()));

    // when
    int count = chatRoomService.countUnreadMessageProcess(ReadUnreadMessageCountQuery.of(buyer.getId()));

    // then
    assertThat(count).isEqualTo(3);
  }

  @Test
  void 채팅방_상세_조회() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).get();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).get();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(0);
    Trade trade = createMockTrade(post.getId(), seller.getId(), buyer.getId());
    CreateChatRoomCommand command = of(post.getId(), trade.getId(), buyer.getId());
    CreateChatRoomResponse created = chatRoomService.createChatRoomProcess(command);
    ChatRoom chatRoom = chatRoomRepository.findById(created.chatRoomId()).orElseThrow();
    ReadChatRoomDetailQuery query = ReadChatRoomDetailQuery.of(chatRoom.getId(), buyer.getId());

    // when
    ChatRoomDetailResponse response = chatRoomService.readChatRoomDetailProcess(query);

    // then
    assertThat(response.chatroomId()).isEqualTo(chatRoom.getId());
    assertThat(response.partner().id()).isEqualTo(seller.getId());
    assertThat(response.post().id()).isEqualTo(post.getId());
    assertThat(response.trade().status()).isEqualTo("ACCEPTED");
  }

  @Test
  void 채팅방_상세_조회_시_채팅방에_속하지_않은_회원이_조회하면_예외가_발생한다() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).get();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).get();
    Member stranger = memberRepository.save(otherMember()); // 제3자
    Post post = postRepository.findAllBySellerId(seller.getId()).get(0);
    Trade trade = createMockTrade(post.getId(), seller.getId(), buyer.getId());
    CreateChatRoomCommand command = of(post.getId(), trade.getId(), buyer.getId());
    CreateChatRoomResponse created = chatRoomService.createChatRoomProcess(command);
    ChatRoom chatRoom = chatRoomRepository.findById(created.chatRoomId()).orElseThrow();
    ReadChatRoomDetailQuery query = ReadChatRoomDetailQuery.of(chatRoom.getId(), stranger.getId());

    // when & then
    assertThatThrownBy(() -> chatRoomService.readChatRoomDetailProcess(query))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_PARTICIPATED_CHAT_ROOM.getMessage());
  }

  @Test
  void 채팅_내역_조회() throws InterruptedException {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).orElseThrow();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).orElseThrow();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(6);
    Trade trade = createMockTrade(post.getId(), seller.getId(), buyer.getId());
    CreateChatRoomCommand command = of(post.getId(), trade.getId(), buyer.getId());
    Long chatRoomId = chatRoomService.createChatRoomProcess(command).chatRoomId();
    Thread.sleep(1000);
    chatRoomService.createChatRoomMessageProcess(CUSTOM_WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND(chatRoomId, buyer.getId()));
    chatRoomService.createChatRoomMessageProcess(CUSTOM_WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND(chatRoomId, seller.getId()));

    // when
    ChatMessagesResponse response = chatRoomService.readChatMessagesProcess(ReadChatMessagesQuery.of(buyer.getId(), chatRoomId));

    // then
    assertThat(response.messages()).hasSize(2);
    assertThat(response.messages().get(0).isMine()).isTrue(); // buyer가 보낸 메시지
    assertThat(response.messages().get(1).isMine()).isFalse(); // seller가 보낸 메시지
  }

  @Test
  void 채팅_내역_조회_시_채팅방에_참여하지_않은_회원이면_예외가_발생한다() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).orElseThrow();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).orElseThrow();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(0);
    Trade trade = createMockTrade(post.getId(), seller.getId(), buyer.getId());
    CreateChatRoomCommand command = of(post.getId(), trade.getId(), buyer.getId());
    Long chatRoomId = chatRoomService.createChatRoomProcess(command).chatRoomId();
    ChatRoomMember exited = chatRoomMemberRepository.findByChatRoomIdAndMemberId(chatRoomId, buyer.getId()).orElseThrow();
    exited.updateExitedAt(LocalDateTime.now());

    // when & then
    assertThatThrownBy(() -> chatRoomService.readChatMessagesProcess(ReadChatMessagesQuery.of(buyer.getId(), chatRoomId)))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_PARTICIPATED_CHAT_ROOM.getMessage());
  }

  @Test
  void 채팅방_입장_시_메시지_읽음_처리() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).orElseThrow();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).orElseThrow();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(2);
    Trade trade = createMockTrade(post.getId(), seller.getId(), buyer.getId());
    CreateChatRoomCommand command = of(post.getId(), trade.getId(), buyer.getId());
    CreateChatRoomResponse chatRoom = chatRoomService.createChatRoomProcess(command);
    Long chatRoomId = chatRoom.chatRoomId();
    CreateChatMessageCommand messageCommand = CUSTOM_WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND(chatRoomId, seller.getId());
    chatRoomService.createChatRoomMessageProcess(messageCommand);

    // 사전 확인
    List<ChatMessage> before = chatMessageRepository.findAllByChatRoomId(chatRoomId);
    assertThat(before).hasSize(1);
    assertThat(before.get(0).getIsRead()).isFalse();

    // when
    chatRoomService.enterChatRoomProcess(EnterChatRoomCommand.of(chatRoomId, buyer.getId()));

    // then
    List<ChatMessage> after = chatMessageRepository.findAllByChatRoomId(chatRoomId);
    assertThat(after.get(0).getIsRead()).isTrue();
  }

  private Trade createMockTrade(Long postId, UUID sellerId, UUID buyerId) {
    Trade trade = Trade.create(postId, sellerId, buyerId);
    return tradeRepository.save(trade);
  }
}
