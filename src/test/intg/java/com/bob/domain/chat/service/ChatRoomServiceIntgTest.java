package com.bob.domain.chat.service;

import static com.bob.domain.chat.entity.type.ChatMessageType.SYSTEM;
import static com.bob.domain.chat.service.dto.command.CreateChatMessageCommand.IS_FAR_MEMBER;
import static com.bob.support.fixture.command.CreateChatMessageCommandFixture.CUSTOM_WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND;
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
import com.bob.domain.chat.service.dto.query.ReadChatRoomDetailQuery;
import com.bob.domain.chat.service.dto.query.ReadChatRoomListQuery;
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
import com.bob.support.fixture.command.CreateChatRoomCommandFixture;
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
  @DisplayName("채팅방 생성 - 성공 테스트")
  void 채팅방을_생성할_수_있다() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).get();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).get();

    Post post = postRepository.findAllBySellerId(seller.getId()).get(0);
    CreateChatRoomCommand command = CreateChatRoomCommandFixture.of(post.getId(), buyer.getId());

    // when
    CreateChatRoomResponse response = chatRoomService.createChatRoomProcess(command);

    // then
    ChatRoom chatRoom = chatRoomRepository.findById(response.chatRoomId()).orElseThrow();
    List<ChatRoomMember> members = chatRoomMemberRepository.findByChatRoomId(chatRoom.getId());

    assertThat(chatRoom.getPostId()).isEqualTo(post.getId());
    assertThat(chatRoom.getTitleSuffix()).contains("자바의 정석");
    assertThat(members).extracting(ChatRoomMember::getMemberId)
        .containsExactlyInAnyOrder(seller.getId(), buyer.getId());

    Trade trade = tradeRepository.findById(chatRoom.getTradeId()).orElseThrow();
    assertThat(trade.getSellerId()).isEqualTo(seller.getId());
    assertThat(trade.getBuyerId()).isEqualTo(buyer.getId());
  }

  @Test
  @DisplayName("채팅방 생성 - 거리가 먼 사용자 SYSTEM 메시지 등록 테스트")
  void isFar_사용자가_채팅방을_생성하면_SYSTEM_메시지가_자동으로_저장된다() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).orElseThrow();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).orElseThrow();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(3);
    CreateChatRoomCommand command = new CreateChatRoomCommand(post.getId(), buyer.getId(), true);

    // when
    CreateChatRoomResponse response = chatRoomService.createChatRoomProcess(command);
    Long chatRoomId = response.chatRoomId();

    // then
    List<ChatMessage> messages = chatMessageRepository.findAllByChatRoomId(chatRoomId);
    assertThat(messages).hasSize(1);

    ChatMessage systemMessage = messages.get(0);
    assertThat(systemMessage.getChatMessageType()).isEqualTo(SYSTEM);
    assertThat(systemMessage.getChatMessage()).isEqualTo(IS_FAR_MEMBER);
    assertThat(systemMessage.getIsRead()).isTrue();
    assertThat(systemMessage.getSenderId()).isEqualTo(buyer.getId());
  }

  @Test
  @DisplayName("채팅방 생성 - 실패 테스트 (본인 게시글 요청)")
  void 본인_게시글에는_채팅방을_생성할_수_없다() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).get();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(0);
    CreateChatRoomCommand command = CreateChatRoomCommandFixture.of(post.getId(), seller.getId());

    // when & then
    assertThatThrownBy(() -> chatRoomService.createChatRoomProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.IS_SAME_CHAT_MEMBER.getMessage());
  }

  @Test
  @DisplayName("채팅방 생성 - 이미 존재하는 경우 기존 ID 반환")
  void 이미_존재하는_채팅방이_있다면_ID를_반환한다() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).get();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).get();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(0);

    CreateChatRoomCommand command = CreateChatRoomCommandFixture.of(post.getId(), buyer.getId()); // 최초 생성
    CreateChatRoomResponse created = chatRoomService.createChatRoomProcess(command);

    // when
    CreateChatRoomResponse result = chatRoomService.createChatRoomProcess(command);

    // then
    assertThat(result.chatRoomId()).isEqualTo(created.chatRoomId());
    assertThat(chatRoomRepository.findById(result.chatRoomId())).isNotNull();
    assertThat(chatRoomMemberRepository.findByChatRoomId(result.chatRoomId())).hasSize(2);
  }

  @DisplayName("채팅 메시지 전송 - 성공 테스트")
  @Test
  void 채팅_메시지를_전송한다() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).orElseThrow();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).orElseThrow();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(5);
    CreateChatRoomResponse chatRoomResponse = chatRoomService.createChatRoomProcess(
        CreateChatRoomCommandFixture.of(post.getId(), buyer.getId())
    );
    Long chatRoomId = chatRoomResponse.chatRoomId();
    CreateChatMessageCommand command = CUSTOM_WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND(chatRoomId, buyer.getId());

    // when
    chatRoomService.createChatRoomMessageProcess(command);

    // then
    List<ChatMessage> messages = chatMessageRepository.findAllByChatRoomId(chatRoomId);
    assertThat(messages).hasSize(1);

    ChatMessage saved = messages.get(0);
    assertThat(saved.getChatMessage()).isEqualTo("message");
    assertThat(saved.getChatMessageType().name()).isEqualTo("MIX");
  }

  @Test
  @DisplayName("채팅 메시지 전송 - 비활성화 된 채팅방 활성화 테스트")
  void 비활성화된_채팅방에_첫_메시지를_보내면_채팅방이_활성화된다() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).orElseThrow();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).orElseThrow();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(4);

    CreateChatRoomCommand command = CreateChatRoomCommandFixture.of(post.getId(), buyer.getId());
    CreateChatRoomResponse response = chatRoomService.createChatRoomProcess(command);
    Long chatRoomId = response.chatRoomId();

    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
    assertThat(chatRoom.getEnableStatus()).isFalse();

    CreateChatMessageCommand messageCommand = CUSTOM_WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND(chatRoomId, buyer.getId());

    // when
    chatRoomService.createChatRoomMessageProcess(messageCommand);

    // then
    ChatRoom updated = chatRoomRepository.findById(chatRoomId).orElseThrow();
    assertThat(updated.getEnableStatus()).isTrue();
  }

  @Test
  @DisplayName("채팅방 목록 조회 - 성공 테스트")
  void 활성화된_채팅방_목록만_반환한다() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).get();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).get();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(0);
    CreateChatRoomCommand command1 = CreateChatRoomCommandFixture.of(post.getId(), buyer.getId());
    CreateChatRoomResponse response1 = chatRoomService.createChatRoomProcess(command1);
    CreateChatRoomCommand command2 = CreateChatRoomCommandFixture.of(post.getId(), buyer.getId());
    chatRoomService.createChatRoomProcess(command2);

    ChatRoom enableChatRoom = chatRoomRepository.findById(response1.chatRoomId()).get();
    enableChatRoom.updateChatRoomStatus(true); // 채팅방 활성화

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
  @DisplayName("채팅방 상세 조회 - 성공 테스트")
  void 채팅방_상세정보를_정상적으로_조회할_수_있다() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).get();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).get();

    Post post = postRepository.findAllBySellerId(seller.getId()).get(0);

    CreateChatRoomCommand command = CreateChatRoomCommandFixture.of(post.getId(), buyer.getId());
    CreateChatRoomResponse created = chatRoomService.createChatRoomProcess(command);

    ChatRoom chatRoom = chatRoomRepository.findById(created.chatRoomId()).orElseThrow();
    chatRoom.updateChatRoomStatus(true); // 활성화

    ReadChatRoomDetailQuery query = ReadChatRoomDetailQuery.of(chatRoom.getId(), buyer.getId());

    // when
    ChatRoomDetailResponse response = chatRoomService.readChatRoomDetailProcess(query);

    // then
    assertThat(response.chatroomId()).isEqualTo(chatRoom.getId());
    assertThat(response.partner().id()).isEqualTo(seller.getId());
    assertThat(response.post().id()).isEqualTo(post.getId());
    assertThat(response.trade().status()).isEqualTo("REQUESTED");
  }

  @Test
  @DisplayName("채팅방 상세 조회 - 채팅방에 속하지 않은 경우 예외가 발생한다")
  void 채팅방에_속하지_않은_회원은_예외가_발생한다() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).get();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).get();
    Member stranger = memberRepository.save(otherMember()); // 제3자

    Post post = postRepository.findAllBySellerId(seller.getId()).get(0);
    CreateChatRoomCommand command = CreateChatRoomCommandFixture.of(post.getId(), buyer.getId());
    CreateChatRoomResponse created = chatRoomService.createChatRoomProcess(command);

    ChatRoom chatRoom = chatRoomRepository.findById(created.chatRoomId()).orElseThrow();

    ReadChatRoomDetailQuery query = ReadChatRoomDetailQuery.of(chatRoom.getId(), stranger.getId());

    // when & then
    assertThatThrownBy(() -> chatRoomService.readChatRoomDetailProcess(query))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_PARTICIPATED_CHAT_ROOM.getMessage());
  }

  @Test
  @DisplayName("채팅방 입장 - 메시지 읽음 처리 테스트")
  void 채팅방_입장_시_해당_채팅방의_안_읽은_메시지를_읽음_처리한다() {
    // given
    Member seller = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0")).orElseThrow();
    Member buyer = memberRepository.findById(UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59")).orElseThrow();
    Post post = postRepository.findAllBySellerId(seller.getId()).get(2);
    CreateChatRoomCommand command = CreateChatRoomCommandFixture.of(post.getId(), buyer.getId());
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

  // TODO: 채팅방 나가기 기능의 통합 테스트 작성
}
