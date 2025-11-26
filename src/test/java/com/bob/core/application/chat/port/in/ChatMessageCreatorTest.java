package com.bob.core.application.chat.port.in;

import static com.bob.global.exception.response.ApplicationError.NOT_PARTICIPATED_CHAT_ROOM;
import static com.bob.support.fixture.chat.domain.ChatRoomFixture.createChatroom;
import static com.bob.support.fixture.chat.dto.command.ChatMessageCommandFixture.createMessageCommand;
import static com.bob.support.fixture.chat.dto.command.ChatMessageCommandFixture.createSystemMessageCommand;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.domain.chat.ChatMessage;
import com.bob.core.domain.chat.Chatroom;
import com.bob.core.domain.chat.repository.ChatroomRepository;
import com.bob.core.domain.chat.type.ChatMessageType;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@ContainerTest
@DisplayName("채팅 메시지 생성 테스트")
record ChatMessageCreatorTest(ChatMessageCreator chatMessageCreator, ChatroomRepository chatroomRepository) {

    @Test
    void 채팅_메시지_생성() {
        Chatroom chatroom = createChatroom();
        Chatroom saved = chatroomRepository.save(chatroom);

        var command = createMessageCommand();

        ChatMessage message = chatMessageCreator.createChatMessage(saved.getId(), command);

        assertThat(message).isNotNull();
        assertThat(message.getId()).isNotNull();
        assertThat(message.getSenderId()).isEqualTo(MEMBER_ID);
        assertThat(message.getType()).isEqualTo(ChatMessageType.TEXT);
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
            .hasMessage(NOT_PARTICIPATED_CHAT_ROOM.getMessage());
    }

    @Test
    void 채팅_메시지_생성_시_퇴장한_회원은_사용자_예외가_발생한다() {
        Chatroom chatroom = createChatroom();
        chatroom.exitMember(MEMBER_ID);
        Chatroom saved = chatroomRepository.save(chatroom);

        assertThatThrownBy(() -> chatMessageCreator.createChatMessage(saved.getId(), createMessageCommand()))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(NOT_PARTICIPATED_CHAT_ROOM.getMessage());
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
        assertThat(message.getIsRead()).isTrue();
    }

    @Test
    void 채팅_시스템_메시지_생성_시_채팅방이_없으면_null_반환() {
        var command = createSystemMessageCommand("시스템 메시지");

        ChatMessage message = chatMessageCreator.createSystemChatMessage(command);

        assertThat(message).isNull();
    }
}
