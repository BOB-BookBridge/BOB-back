package com.bob.support.fixture.response;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.chat.service.dto.response.ChatPostResponse;

public class ChatPostResponseFixture {

  public static ChatPostResponse DEFAULT_CHAT_POST_RESPONSE() {
    return ChatPostResponse.of(
        1L,
        MEMBER_ID,
        "객체지향의 사실과 오해",
        "image.png"
    );
  }
}
