package com.bob.core.chat.application.port.in;

import static com.bob.global.event.sse.manager.type.EmitterType.CHAT;
import static com.bob.global.event.sse.repository.chat.ChatEmitterKey.of;
import static com.bob.global.exception.response.ApplicationError.CHATROOM_ACCESS_DENIED;
import static com.bob.support.fixture.chat.domain.ChatroomFixture.createChatroom;
import static com.bob.support.fixture.chat.dto.command.ChatMessageCommandFixture.createMessageCommand;
import static com.bob.support.fixture.chat.dto.command.ChatMessageCommandFixture.createSystemMessageCommand;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.bob.core.chat.application.dto.result.ChatMessageCreationResult;
import com.bob.core.chat.domain.ChatMessage;
import com.bob.core.chat.domain.Chatroom;
import com.bob.core.chat.domain.repository.ChatroomRepository;
import com.bob.core.chat.domain.type.ChatMessageType;
import com.bob.global.event.sse.manager.EmitterManager;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("채팅 메시지 생성 테스트")
@ContainerTest
@RequiredArgsConstructor
class ChatMessageCreatorTest {

    final ChatMessageCreator chatMessageCreator;
    final ChatroomRepository chatroomRepository;

    @MockitoBean
    final EmitterManager emitterManager;

    @Test
    void 채팅_메시지_전송() {
        Chatroom chatroom = chatroomRepository.save(createChatroom());

        var command = createMessageCommand();

        ChatMessageCreationResult result = chatMessageCreator.createChatMessage(chatroom.getId(), command);

        assertThat(result).isNotNull();
        assertThat(result.message()).isNotNull();
        assertThat(result.message().getId()).isNotNull();
        assertThat(result.message().getSenderId()).isEqualTo(MEMBER_ID);
        assertThat(result.message().getType()).isEqualTo(ChatMessageType.TEXT);
        assertThat(result.partnerLastReadMessageId()).isNull();
    }

    @Test
    void 채팅_메시지_전송_시_상대방이_채팅방_입장중인_경우_lastReadMessageId_갱신() {
        Chatroom chatroom = chatroomRepository.save(createChatroom());

        var command = createMessageCommand();

        given(emitterManager.isExistConnection(CHAT, of(chatroom.getId(), chatroom.getPartnerId(MEMBER_ID))))
            .willReturn(true);

        ChatMessageCreationResult result = chatMessageCreator.createChatMessage(chatroom.getId(), command);

        ChatMessage message = result.message();

        assertThat(result).isNotNull();
        assertThat(message).isNotNull();
        assertThat(message.getId()).isNotNull();
        assertThat(message.getSenderId()).isEqualTo(MEMBER_ID);
        assertThat(message.getType()).isEqualTo(ChatMessageType.TEXT);
        assertThat(result.partnerLastReadMessageId()).isEqualTo(message.getId());
    }

    @Test
    void 채팅_메시지_전송_시_상대방이_나간_경우_자동_재입장_처리() {
        Chatroom chatroom = createChatroom();
        chatroom.exitMember(OTHER_MEMBER_ID);
        Chatroom saved = chatroomRepository.save(chatroom);

        chatMessageCreator.createChatMessage(saved.getId(), createMessageCommand());

        Chatroom result = chatroomRepository.findById(saved.getId()).orElseThrow();
        assertThat(result.isMemberExited(OTHER_MEMBER_ID)).isFalse();
    }

    @Test
    void 채팅_메시지_생성_시_참여중이지_않으면_사용자_예외가_발생한다() {
        Chatroom chatroom = createChatroom();
        Chatroom saved = chatroomRepository.save(chatroom);

        UUID nonParticipantId = UUID.randomUUID();

        assertThatThrownBy(() -> chatMessageCreator.createChatMessage(saved.getId(),
            createMessageCommand(nonParticipantId, "메시지", null)))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(CHATROOM_ACCESS_DENIED.getMessage());
    }

    @Test
    void 채팅_메시지_생성_시_퇴장한_회원은_사용자_예외가_발생한다() {
        Chatroom chatroom = createChatroom();
        chatroom.exitMember(MEMBER_ID);
        Chatroom saved = chatroomRepository.save(chatroom);

        assertThatThrownBy(() -> chatMessageCreator.createChatMessage(saved.getId(), createMessageCommand()))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(CHATROOM_ACCESS_DENIED.getMessage());
    }

    @Test
    void 채팅_시스템_메시지_생성() {
        Chatroom chatroom = createChatroom();
        chatroomRepository.save(chatroom);

        var command = createSystemMessageCommand("시스템 메시지");

        ChatMessage message = chatMessageCreator.createSystemChatMessage(command);

        assertThat(message).isNotNull();
        assertThat(message.getId()).isNotNull();
        assertThat(message.getContent()).isEqualTo("시스템 메시지");
        assertThat(message.getType()).isEqualTo(ChatMessageType.SYSTEM);
    }

    @Test
    void 채팅_시스템_메시지_생성_시_채팅방이_없으면_null_반환() {
        var command = createSystemMessageCommand("시스템 메시지");

        ChatMessage message = chatMessageCreator.createSystemChatMessage(command);

        assertThat(message).isNull();
    }
}
