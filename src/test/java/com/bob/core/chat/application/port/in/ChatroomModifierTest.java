package com.bob.core.chat.application.port.in;

import static com.bob.core.chat.domain.type.ChatMessageType.TEXT;
import static com.bob.support.fixture.chat.domain.ChatroomFixture.createChatroom;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.chat.application.dto.command.DeactivateChatroomCommand;
import com.bob.core.chat.application.dto.command.ExitChatroomCommand;
import com.bob.core.chat.application.dto.command.JoinChatroomCommand;
import com.bob.core.chat.domain.ChatMessage;
import com.bob.core.chat.domain.Chatroom;
import com.bob.core.chat.domain.repository.ChatroomRepository;
import com.bob.support.annotation.ContainerTest;

@ContainerTest
@DisplayName("채팅방 수정 테스트")
record ChatroomModifierTest(ChatroomModifier chatroomModifier, ChatroomRepository chatroomRepository) {

    @Test
    void 채팅방_입장() {
        Chatroom chatroom = createChatroom();
        chatroom.addMessage(OTHER_MEMBER_ID, "메시지1", TEXT);
        chatroom.addMessage(OTHER_MEMBER_ID, "메시지2", TEXT);
        Chatroom saved = chatroomRepository.save(chatroom);

        var command = new JoinChatroomCommand(MEMBER_ID);

        chatroomModifier.join(saved.getId(), command);

        Chatroom result = chatroomRepository.findById(saved.getId()).orElseThrow();
        assertThat(result.countUnreadMessages(MEMBER_ID)).isZero();
    }

    @Test
    void 채팅방_퇴장() {
        Chatroom chatroom = createChatroom();
        chatroom.addMessage(OTHER_MEMBER_ID, "메시지1", TEXT);
        Chatroom saved = chatroomRepository.save(chatroom);

        var command = new ExitChatroomCommand(MEMBER_ID);

        chatroomModifier.exit(saved.getId(), command);

        Chatroom result = chatroomRepository.findById(saved.getId()).orElseThrow();
        assertThat(result.isMemberExited(MEMBER_ID)).isTrue();
        assertThat(result.countUnreadMessages(MEMBER_ID)).isZero();
    }

    @Test
    void 채팅방_비활성화() {
        Chatroom chatroom = createChatroom();
        ChatMessage message = chatroom.addMessage(OTHER_MEMBER_ID, "메시지1", TEXT);
        chatroomRepository.save(chatroom);

        assertThat(chatroom.isActive()).isTrue();

        var command = new DeactivateChatroomCommand(message.getId());

        chatroomModifier.deactivate(command);

        assertThat(chatroom.isActive()).isFalse();
    }
}
