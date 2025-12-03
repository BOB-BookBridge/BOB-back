package com.bob.core.application.chat.port.in;

import static com.bob.global.exception.response.ApplicationError.CHATROOM_ACCESS_DENIED;
import static com.bob.support.fixture.chat.domain.ChatroomFixture.createChatroom;
import static com.bob.support.fixture.chat.dto.query.ValidateParticipateQueryFixture.validateParticipantQuery;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.chat.dto.query.ReadChatroomByPostAndMemberQuery;
import com.bob.core.application.chat.dto.query.ReadChatroomDetailQuery;
import com.bob.core.application.chat.dto.query.ReadChatroomSummariesQuery;
import com.bob.core.application.chat.dto.query.ValidateParticipantQuery;
import com.bob.core.application.chat.dto.result.ChatroomDetail;
import com.bob.core.application.chat.dto.result.ChatroomSummary;
import com.bob.core.domain.chat.Chatroom;
import com.bob.core.domain.chat.repository.ChatroomRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@ContainerTest
@DisplayName("채팅방 조회 테스트")
record ChatroomReaderTest(ChatroomReader chatroomReader, ChatroomRepository chatroomRepository) {

    @Test
    void ID_기반_채팅방_조회() {
        Chatroom chatroom = createChatroom();
        Chatroom saved = chatroomRepository.save(chatroom);

        Chatroom result = chatroomReader.read(saved.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(saved.getId());
    }

    @Test
    void ID_기반_채팅방_조회_시_존재하지_않으면_예외가_발생한다() {
        Long nonExistentId = 999L;

        assertThatThrownBy(() -> chatroomReader.read(nonExistentId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("채팅방을 찾을 수 없습니다.");
    }

    @Test
    void 게시글_구매자_기반_채팅방_조회() {
        Chatroom chatroom = createChatroom();
        chatroomRepository.save(chatroom);

        var query = new ReadChatroomByPostAndMemberQuery(1L, OTHER_MEMBER_ID);

        Optional<Chatroom> result = chatroomReader.readByPostAndBuyer(query);

        assertThat(result).isPresent();
    }

    @Test
    void 채팅방_목록_조회() {
        Chatroom chatroom = createChatroom();
        chatroomRepository.save(chatroom);

        var query = new ReadChatroomSummariesQuery(MEMBER_ID);

        List<ChatroomSummary> summaries = chatroomReader.readChatRoomSummaries(query);

        assertThat(summaries).isNotEmpty();
    }

    @Test
    void 채팅방_상세_조회() {
        Chatroom chatroom = createChatroom();
        Chatroom saved = chatroomRepository.save(chatroom);

        var query = new ReadChatroomDetailQuery(MEMBER_ID);

        ChatroomDetail detail = chatroomReader.readChatRoomDetail(saved.getId(), query);

        assertThat(detail).isNotNull();
    }

    @Test
    void 채팅방_참여자_검증() {
        Chatroom chatroom = createChatroom();
        Chatroom saved = chatroomRepository.save(chatroom);

        var query = validateParticipantQuery();

        chatroomReader.validateParticipant(saved.getId(), query);
    }

    @Test
    void 채팅방_참여자_검증_시_참여하지_않는_사용자면_사용자_예외가_발생한다() {
        Chatroom chatroom = createChatroom();
        Chatroom saved = chatroomRepository.save(chatroom);

        ValidateParticipantQuery query = validateParticipantQuery(UUID.randomUUID());

        assertThatThrownBy(() -> chatroomReader.validateParticipant(saved.getId(), query))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(CHATROOM_ACCESS_DENIED.getMessage());
    }

    @Test
    void 채팅방_참여자_검증_시_퇴장한_회원이라면_사용자_예외가_발생한다() {
        Chatroom chatroom = createChatroom();
        chatroom.exitMember(MEMBER_ID);
        Chatroom saved = chatroomRepository.save(chatroom);

        assertThatThrownBy(() -> chatroomReader.validateParticipant(saved.getId(), validateParticipantQuery()))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(CHATROOM_ACCESS_DENIED.getMessage());
    }
}
