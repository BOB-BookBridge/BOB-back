package com.bob.core.application.chat.port.in;

import static com.bob.core.domain.chat.type.ChatMessageType.TEXT;
import static com.bob.support.fixture.chat.domain.ChatRoomFixture.createChatroom;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.chat.dto.command.ExitChatroomCommand;
import com.bob.core.application.chat.dto.command.JoinChatroomCommand;
import com.bob.core.domain.chat.Chatroom;
import com.bob.core.domain.chat.repository.ChatroomRepository;
import com.bob.support.annotation.ContainerTest;

@ContainerTest
@DisplayName("채팅방 수정 테스트")
record ChatroomModifierTest(ChatroomModifier chatroomModifier, ChatroomRepository chatroomRepository) {

    @Test
    void 채팅방_입장() {
        Chatroom chatroom = createChatroom();
        chatroom.addMessage(OTHER_MEMBER_ID, "메시지1", TEXT, false);
        chatroom.addMessage(OTHER_MEMBER_ID, "메시지2", TEXT, false);
        Chatroom saved = chatroomRepository.save(chatroom);

        var command = new JoinChatroomCommand(MEMBER_ID);

        chatroomModifier.join(saved.getId(), command);

        Chatroom result = chatroomRepository.findById(saved.getId()).orElseThrow();
        assertThat(result.countUnreadMessages(MEMBER_ID)).isZero();
    }

    @Test
    void 채팅방_퇴장() {
        Chatroom chatroom = createChatroom();
        chatroom.addMessage(OTHER_MEMBER_ID, "메시지1", TEXT, false);
        Chatroom saved = chatroomRepository.save(chatroom);

        var command = new ExitChatroomCommand(MEMBER_ID);

        chatroomModifier.exit(saved.getId(), command);

        Chatroom result = chatroomRepository.findById(saved.getId()).orElseThrow();
        assertThat(result.isMemberExited(MEMBER_ID)).isTrue();
        assertThat(result.countUnreadMessages(MEMBER_ID)).isZero();
    }
}
