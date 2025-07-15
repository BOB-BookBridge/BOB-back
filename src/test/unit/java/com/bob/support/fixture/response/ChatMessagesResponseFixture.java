package com.bob.support.fixture.response;

import com.bob.domain.chat.service.dto.response.ChatMessagesResponse;
import com.bob.domain.chat.service.dto.response.internal.ChatFileSummary;
import com.bob.domain.chat.service.dto.response.internal.MessageSummary;
import java.time.LocalDateTime;
import java.util.List;

public class ChatMessagesResponseFixture {

  public static final MessageSummary DEFAULT_CHAT_MESSAGE_SUMMARY =
      new MessageSummary(
          1L,
          "TEXT",
          "메시지",
          List.of(),
          LocalDateTime.of(2024, 1, 1, 12, 0),
          false,
          true
      );

  public static final MessageSummary DEFAULT_CHAT_IMAGE_SUMMARY =
      new MessageSummary(
          2L,
          "IMAGE",
          null,
          List.of(new ChatFileSummary(0, "/chat/test.png")),
          LocalDateTime.of(2024, 1, 1, 12, 5),
          true,
          false
      );

  public static final ChatMessagesResponse DEFAULT_CHAT_MESSAGES_RESPONSE =
      new ChatMessagesResponse(
          List.of(DEFAULT_CHAT_MESSAGE_SUMMARY, DEFAULT_CHAT_IMAGE_SUMMARY),
          true
      );
}
