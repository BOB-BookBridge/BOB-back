package com.bob.web.chat.controller;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.request.CreateChatRoomRequestFixture.DEFAULT_CREATE_CHAT_ROOM_REQUEST;
import static com.bob.support.fixture.response.ChatRoomResponseFixture.DEFAULT_CHATROOM_DETAIL;
import static com.bob.support.fixture.response.ChatRoomResponseFixture.DEFAULT_CHATROOM_SUMMARY_LIST;
import static com.bob.support.fixture.response.ChatRoomResponseFixture.DEFAULT_CREATE_CHATROOM_RESPONSE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mvc = MockMvcBuilders.standaloneSetup(chatRoomController).build();
  }

  @Test
  @DisplayName("채팅방 생성 API를 호출할 수 있다")
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

  @Test
  @DisplayName("채팅방 목록 조회 API를 호출할 수 있다")
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
  @DisplayName("채팅방 상세 조회 API를 호출할 수 있다")
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
        .andExpect(jsonPath("$.post.title").value("책"))
        .andExpect(jsonPath("$.trade.status").value("READY"));

    then(readUseCase).should(times(1)).readChatRoomDetailProcess(any());
  }
}