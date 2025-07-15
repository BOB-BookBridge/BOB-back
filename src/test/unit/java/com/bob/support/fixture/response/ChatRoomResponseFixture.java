package com.bob.support.fixture.response;

import static com.bob.domain.chat.service.dto.response.CreateChatRoomResponse.of;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.chat.service.dto.response.ChatRoomDetailResponse;
import com.bob.domain.chat.service.dto.response.ChatRoomSummaryResponse;
import com.bob.domain.chat.service.dto.response.CreateChatRoomResponse;
import com.bob.domain.chat.service.dto.response.internal.ChatPartnerSummary;
import com.bob.domain.chat.service.dto.response.internal.ChatPostSummary;
import com.bob.domain.chat.service.dto.response.internal.ChatTradeSummary;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ChatRoomResponseFixture {

  public static final Long DEFAULT_CHATROOM_ID = 1L;

  public static CreateChatRoomResponse DEFAULT_CREATE_CHATROOM_RESPONSE = of(DEFAULT_CHATROOM_ID);

  public static ChatRoomDetailResponse DEFAULT_CHATROOM_DETAIL = ChatRoomDetailResponse.builder()
      .chatroomId(DEFAULT_CHATROOM_ID)
      .title("제목")
      .trade(new ChatTradeSummary(1L, "REQUESTED"))
      .post(new ChatPostSummary(1L, "READY", MEMBER_ID, "게시글 제목", "image.png", 10000))
      .partner(new ChatPartnerSummary(
          UUID.fromString("00000000-0000-0000-0000-00000000003a"),
          "booklover",
          "booklover.png"
      ))
      .build();

  public static ChatRoomSummaryResponse DEFAULT_CHATROOM_SUMMARY_1 = ChatRoomSummaryResponse.builder()
      .chatroomId(DEFAULT_CHATROOM_ID)
      .thumbnailUrl("image")
      .lastMessage("책 아직 남아 있나요?")
      .lastMessageAt(LocalDateTime.of(2024, 3, 30, 15, 0))
      .partner(new ChatPartnerSummary(
          UUID.fromString("00000000-0000-0000-0000-00000000003a"),
          "booklover",
          "booklover.png"
      ))
      .unreadCount(3)
      .build();

  public static ChatRoomSummaryResponse DEFAULT_CHATROOM_SUMMARY_2 = ChatRoomSummaryResponse.builder()
      .chatroomId(2L)
      .thumbnailUrl("image")
      .lastMessage("넵! 택배로도 가능해요")
      .lastMessageAt(LocalDateTime.of(2024, 3, 30, 16, 55, 20))
      .partner(new ChatPartnerSummary(
          UUID.fromString("00000000-0000-0000-0000-00000000003d"),
          "javaboy",
          null
      ))
      .unreadCount(0)
      .build();

  public static List<ChatRoomSummaryResponse> DEFAULT_CHATROOM_SUMMARY_LIST =
      List.of(DEFAULT_CHATROOM_SUMMARY_1, DEFAULT_CHATROOM_SUMMARY_2);
}