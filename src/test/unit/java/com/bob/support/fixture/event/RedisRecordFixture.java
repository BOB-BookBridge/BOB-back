package com.bob.support.fixture.event;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;

import com.bob.infra.redis.record.RedisRecord;
import com.bob.infra.redis.record.RedisRecord.Sender;
import java.time.LocalDateTime;
import java.util.List;

public class RedisRecordFixture {

  public static final RedisRecord CHAT_TEXT_RECORD = RedisRecord.builder()
      .receiverId(OTHER_MEMBER_ID)
      .type("CHAT")
      .refId("1")
      .childId("1")
      .body("안녕")
      .normalize(false)
      .sender(Sender.of(MEMBER_ID, "tester", "/profile/1.png"))
      .build();

  public static final RedisRecord CHAT_IMAGE_RECORD = RedisRecord.builder()
      .receiverId(OTHER_MEMBER_ID)
      .type("CHAT")
      .refId("1")
      .childId("1")
      .body(null)
      .fileNames(List.of("/chat/test.png"))
      .normalize(true)
      .sender(Sender.of(MEMBER_ID, "tester", "/profile/1.png"))
      .build();

  public static final RedisRecord CHAT_MIX_RECORD = RedisRecord.builder()
      .receiverId(OTHER_MEMBER_ID)
      .type("CHAT")
      .refId("1")
      .childId("1")
      .body("MIX 테스트 메시지")
      .fileNames(List.of("/chat/test.png"))
      .normalize(true)
      .sender(Sender.of(MEMBER_ID, "tester", "/profile/1.png"))
      .build();

  public static final RedisRecord TRADE_RECORD = RedisRecord.builder()
      .receiverId(OTHER_MEMBER_ID)
      .type("TRADE")
      .refId("1")
      .childId("1")
      .body("거래 완료")
      .fileNames(null)
      .normalize(false)
      .sentAt(LocalDateTime.now())
      .sender(Sender.of(MEMBER_ID, "tester", "/profile/1.png"))
      .build();
}
