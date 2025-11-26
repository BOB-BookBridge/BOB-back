package com.bob.core.application.chat.port.in;

import static com.bob.core.domain.chat.type.ChatMessageType.TEXT;
import static com.bob.core.domain.file.type.FileDomain.CHAT;
import static com.bob.global.exception.response.ApplicationError.NOT_PARTICIPATED_CHAT_ROOM;
import static com.bob.support.fixture.chat.domain.ChatRoomFixture.createChatroom;
import static com.bob.support.fixture.chat.dto.command.ChatMessageCommandFixture.createMessageCommand;
import static com.bob.support.fixture.chat.dto.query.ReadChatMessagesQueryFixture.readChatMessagesQuery;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.chat.dto.command.CreateMessageCommand;
import com.bob.core.application.chat.dto.query.ReadChatMessagesQuery;
import com.bob.core.application.chat.dto.query.ReadUnreadMessageCountQuery;
import com.bob.core.application.chat.dto.result.ChatMessageSummary;
import com.bob.core.domain.chat.Chatroom;
import com.bob.core.domain.chat.repository.ChatroomRepository;
import com.bob.core.domain.file.repository.FileRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.file.domain.FileFixture;

@ContainerTest
@DisplayName("채팅 메시지 조회 테스트")
record ChatMessageReaderTest(
    ChatMessageReader chatMessageReader, ChatMessageCreator chatMessageCreator, ChatroomRepository chatroomRepository,
    FileRepository fileRepository
) {

    @Test
    void 채팅_메시지_목록_조회() {
        Chatroom chatroom = createChatroom();
        chatroomRepository.save(chatroom);
        fileRepository.save(FileFixture.createFile(CHAT, "chat/image.png", 0, chatroom.getId().toString(), MEMBER_ID));

        CreateMessageCommand command1 = createMessageCommand(MEMBER_ID, "사진", List.of("chat/image.png"));
        CreateMessageCommand command2 = createMessageCommand();
        CreateMessageCommand command3 = createMessageCommand();

        chatMessageCreator.createChatMessage(chatroom.getId(), command1);
        chatMessageCreator.createChatMessage(chatroom.getId(), command2);
        chatMessageCreator.createChatMessage(chatroom.getId(), command3);

        var query = readChatMessagesQuery();

        List<ChatMessageSummary> messages = chatMessageReader.readMessages(chatroom.getId(), query);

        assertThat(messages).hasSize(3);
        assertThat(messages.get(0).images().get(0).fileName()).isEqualTo("chat/image.png");
    }

    @Test
    void 채팅_메시지_조회_시_참여하지_않는_사용자면_사용자_예외가_발생한다() {
        Chatroom chatroom = createChatroom();
        Chatroom saved = chatroomRepository.save(chatroom);

        UUID nonParticipantId = UUID.randomUUID();
        ReadChatMessagesQuery query = readChatMessagesQuery(nonParticipantId);

        assertThatThrownBy(() -> chatMessageReader.readMessages(saved.getId(), query))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(NOT_PARTICIPATED_CHAT_ROOM.getMessage());
        ;
    }

    @Test
    void 채팅_메시지_조회_시_퇴장한_회원이면_사용자_예외가_발생한다() {
        Chatroom chatroom = createChatroom();
        chatroom.exitMember(MEMBER_ID);
        Chatroom saved = chatroomRepository.save(chatroom);

        var query = readChatMessagesQuery();

        assertThatThrownBy(() -> chatMessageReader.readMessages(saved.getId(), query))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(NOT_PARTICIPATED_CHAT_ROOM.getMessage());
    }

    @Test
    void 안읽은_메시지_개수_조회() {
        Chatroom chatroom = createChatroom();
        chatroom.addMessage(OTHER_MEMBER_ID, "메시지1", TEXT, false);
        chatroom.addMessage(OTHER_MEMBER_ID, "메시지2", TEXT, false);
        chatroomRepository.save(chatroom);

        ReadUnreadMessageCountQuery query = new ReadUnreadMessageCountQuery(MEMBER_ID);

        int count = chatMessageReader.countUnreadMessagesByMember(query);

        assertThat(count).isEqualTo(2);
    }
}
