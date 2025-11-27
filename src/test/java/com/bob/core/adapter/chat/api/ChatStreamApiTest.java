package com.bob.core.adapter.chat.api;

import static com.bob.support.fixture.chat.domain.ChatroomFixture.createChatroom;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bob.core.domain.chat.Chatroom;
import com.bob.core.domain.chat.repository.ChatroomRepository;
import com.bob.global.event.sse.repository.EmitterRepository;
import com.bob.global.event.sse.repository.chat.ChatEmitterKey;
import com.bob.support.annotation.BobApiTest;

@DisplayName("채팅 구독 API 테스트")
@BobApiTest
record ChatStreamApiTest(
    ChatStreamApi chatStreamApi, ChatroomRepository chatroomRepository,
    EmitterRepository<ChatEmitterKey> emitterRepository
) {

    @Test
    void 채팅_구독() {
        Chatroom chatroom = chatroomRepository.save(createChatroom());
        ChatEmitterKey key = ChatEmitterKey.of(chatroom.getId(), MEMBER_ID);

        SseEmitter emitter = chatStreamApi.subscribe(chatroom.getId(), MEMBER_ID);

        assertThat(emitter).isNotNull();
        assertThat(emitterRepository.exists(key)).isTrue();

        emitter.complete();
    }

    @Test
    void 채팅_구독_시_중복_연결_제거() {
        Chatroom chatroom = chatroomRepository.save(createChatroom());
        ChatEmitterKey key = ChatEmitterKey.of(chatroom.getId(), MEMBER_ID);

        SseEmitter firstEmitter = chatStreamApi.subscribe(chatroom.getId(), MEMBER_ID);
        assertThat(emitterRepository.exists(key)).isTrue();

        SseEmitter secondEmitter = chatStreamApi.subscribe(chatroom.getId(), MEMBER_ID);
        assertThat(emitterRepository.exists(key)).isTrue();
        assertThat(emitterRepository.findAll()).hasSizeGreaterThanOrEqualTo(1);

        assertThat(firstEmitter).isNotEqualTo(secondEmitter);

        secondEmitter.complete();
    }
}
