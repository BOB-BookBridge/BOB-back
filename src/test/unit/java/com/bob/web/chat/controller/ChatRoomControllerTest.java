package com.bob.web.chat.controller;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.request.CreateChatRoomRequestFixture.DEFAULT_CREATE_CHAT_ROOM_REQUEST;
import static com.bob.support.fixture.response.ChatMessagesResponseFixture.DEFAULT_CHAT_MESSAGES_RESPONSE;
import static com.bob.support.fixture.response.ChatRoomResponseFixture.DEFAULT_CHATROOM_DETAIL;
import static com.bob.support.fixture.response.ChatRoomResponseFixture.DEFAULT_CHATROOM_SUMMARY_LIST;
import static com.bob.support.fixture.response.ChatRoomResponseFixture.DEFAULT_CREATE_CHATROOM_RESPONSE;
import static com.bob.web.common.symbol.ResponseSymbol.UPDATED;
import static java.time.LocalDateTime.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bob.domain.chat.service.dto.response.ChatMessageSendResponse;
import com.bob.domain.chat.usecase.ChatRoomModifyUseCase;
import com.bob.domain.chat.usecase.ChatRoomReadUseCase;
import com.bob.domain.chat.usecase.ChatRoomWriteUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("채팅방 API 테스트")
@ExtendWith(MockitoExtension.class)
class ChatRoomControllerTest {

  @InjectMocks
  private ChatRoomController chatRoomController;

  @Mock
  private ChatRoomWriteUseCase writeUseCase;

  @Mock
  private ChatRoomReadUseCase readUseCase;

  @Mock
  private ChatRoomModifyUseCase memberModifyUseCase;

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mvc = MockMvcBuilders.standaloneSetup(chatRoomController).build();
  }

  @Test
  @DisplayName("채팅방 생성 API 호출 테스트")
  void 채팅방_생성_API를_호출할_수_있다() throws Exception {
    // given
    String json = DEFAULT_CREATE_CHAT_ROOM_REQUEST();
    given(writeUseCase.createChatRoomProcess(any())).willReturn(DEFAULT_CREATE_CHATROOM_RESPONSE);

    // when & then
    mvc.perform(post("/chatrooms")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .requestAttr("memberId", MEMBER_ID))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.chatRoomId").value(1L));

    then(writeUseCase).should(times(1)).createChatRoomProcess(any());
  }

  @DisplayName("채팅 메시지 전송 API 호출 테스트")
  @Test
  void 채팅_메시지_전송_API를_호출할_수_있다() throws Exception {
    // given
    String json = """
      {
        "message": "안녕하세요, 거래 가능할까요?",
        "fileNames": []
      }
      """;
    given(writeUseCase.createChatRoomMessageProcess(any())).willReturn(ChatMessageSendResponse.of(1L, false, now()));

    // when & then
    mvc.perform(post("/chatrooms/{chatroomId}/messages", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .requestAttr("memberId", MEMBER_ID))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.isRead").value(false));

    then(writeUseCase).should(times(1)).createChatRoomMessageProcess(any());
  }

  @Test
  @DisplayName("채팅방 목록 조회 API 호출 테스트")
  void 채팅방_목록_조회_API를_호출할_수_있다() throws Exception {
    // given
    given(readUseCase.readChatRoomListProcess(any())).willReturn(DEFAULT_CHATROOM_SUMMARY_LIST);

    // when & then
    mvc.perform(get("/chatrooms")
            .requestAttr("memberId", MEMBER_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].chatroomId").value(1))
        .andExpect(jsonPath("$[0].partner.nickname").value("booklover"));

    then(readUseCase).should(times(1)).readChatRoomListProcess(any());
  }

  @Test
  @DisplayName("읽지 않은 메시지 개수 조회 API 호출 테스트")
  void 읽지_않은_메시지_개수를_정상적으로_조회할_수_있다() throws Exception {
    // given
    int unreadCount = 5;
    given(readUseCase.countUnreadMessageProcess(any())).willReturn(unreadCount);

    // when & then
    mvc.perform(get("/chatrooms/messages/unread")
            .requestAttr("memberId", MEMBER_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.unreadCount").value(unreadCount));

    // verify
    then(readUseCase).should(times(1)).countUnreadMessageProcess(any());
  }

  @Test
  @DisplayName("채팅방 상세 조회 API 호출 테스트")
  void 채팅방_상세_조회_API를_호출할_수_있다() throws Exception {
    // given
    Long chatRoomId = 1L;
    given(readUseCase.readChatRoomDetailProcess(any())).willReturn(DEFAULT_CHATROOM_DETAIL);

    // when & then
    mvc.perform(get("/chatrooms/{chatroomId}", chatRoomId)
            .requestAttr("memberId", MEMBER_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.chatroomId").value(chatRoomId))
        .andExpect(jsonPath("$.partner.nickname").value("booklover"))
        .andExpect(jsonPath("$.post.status").value("READY"))
        .andExpect(jsonPath("$.post.sellerId").value(MEMBER_ID.toString()))
        .andExpect(jsonPath("$.post.title").value("게시글 제목"))
        .andExpect(jsonPath("$.trade.status").value("REQUESTED"));

    then(readUseCase).should(times(1)).readChatRoomDetailProcess(any());
  }

  @Test
  @DisplayName("채팅 메시지 목록 조회 API 호출 테스트")
  void 채팅_메시지_목록_조회_API를_호출할_수_있다() throws Exception {
    // given
    Long chatRoomId = 1L;
    Long beforeMessageId = 100L;
    int size = 10;

    given(readUseCase.readChatMessagesProcess(any())).willReturn(DEFAULT_CHAT_MESSAGES_RESPONSE);

    // when & then
    mvc.perform(get("/chatrooms/{chatRoomId}/messages", chatRoomId)
            .param("beforeMessageId", beforeMessageId.toString())
            .param("size", String.valueOf(size))
            .requestAttr("memberId", MEMBER_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.messages.length()").value(2))
        .andExpect(jsonPath("$.messages[0].id").value(1))
        .andExpect(jsonPath("$.messages[0].content").value("메시지"))
        .andExpect(jsonPath("$.messages[0].isMine").value(true));

    then(readUseCase).should(times(1)).readChatMessagesProcess(any());
  }

  @Test
  @DisplayName("채팅방 나가기 API 호출 테스트")
  void 채팅방_나가기_API를_호출할_수_있다() throws Exception {
    // given
    Long chatRoomId = 1L;

    // when & then
    mvc.perform(patch("/chatrooms/{chatroomId}", chatRoomId)
            .requestAttr("memberId", MEMBER_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.result").value(UPDATED.name()));

    then(memberModifyUseCase).should(times(1)).exitChatRoomProcess(any());
  }
}