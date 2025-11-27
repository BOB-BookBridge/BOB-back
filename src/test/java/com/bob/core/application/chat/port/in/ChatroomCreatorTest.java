package com.bob.core.application.chat.port.in;

import static com.bob.support.fixture.chat.domain.ChatroomFixture.createChatroom;
import static com.bob.support.fixture.chat.dto.command.CreateChatroomCommandFixture.createChatroomCommand;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.domain.chat.Chatroom;
import com.bob.core.domain.chat.repository.ChatroomRepository;
import com.bob.support.annotation.ContainerTest;

@ContainerTest
@DisplayName("채팅방 생성 테스트")
record ChatroomCreatorTest(ChatroomCreator chatroomCreator, ChatroomRepository chatroomRepository) {

    @Test
    void 채팅방_생성() {
        var command = createChatroomCommand();

        Chatroom chatroom = chatroomCreator.create(command);

        assertThat(chatroom).isNotNull();
        assertThat(chatroom.getId()).isNotNull();
        assertThat(chatroom.getMembers()).hasSize(2);
    }

    @Test
    void 채팅방_생성_시_거리가_먼_경우_시스템_메시지_추가() {
        var command = createChatroomCommand(1L, 1L, OTHER_MEMBER_ID, true);

        Chatroom chatroom = chatroomCreator.create(command);

        assertThat(chatroom.getMessages()).hasSize(1);
        assertThat(chatroom.getMessages().get(0).getContent()).contains("거리가 먼 사용자와의 채팅입니다.");
    }

    @Test
    void 채팅방_생성_시_이미_존재하면_기존_채팅방_반환() {
        Chatroom existing = createChatroom();
        chatroomRepository.save(existing);

        existing.exitMember(MEMBER_ID);

        Chatroom chatroom = chatroomCreator.create(createChatroomCommand());

        assertThat(chatroom.getId()).isEqualTo(existing.getId());
        assertThat(chatroom.isMemberExited(MEMBER_ID)).isFalse();
    }
}
