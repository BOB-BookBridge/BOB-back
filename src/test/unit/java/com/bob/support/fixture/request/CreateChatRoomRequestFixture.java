package com.bob.support.fixture.request;

public class CreateChatRoomRequestFixture {

  public static String DEFAULT_CREATE_CHAT_ROOM_REQUEST() {
    return """
        {
          "postId": 1,
          "isFar": false
        }
        """;
  }
}
