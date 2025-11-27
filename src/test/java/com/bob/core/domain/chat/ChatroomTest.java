package com.bob.core.domain.chat;

import static com.bob.core.domain.chat.type.ChatMessageType.IMAGE;
import static com.bob.core.domain.chat.type.ChatMessageType.TEXT;
import static com.bob.support.fixture.chat.domain.ChatMessageFixture.addMessage;
import static com.bob.support.fixture.chat.domain.ChatroomFixture.createChatroom;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("채팅방 도메인 테스트")
class ChatroomTest {

    @Test
    void 채팅방_생성() {
        Long postId = 1L;
        Long tradeId = 1L;
        String titleSuffix = "suffix";

        Chatroom chatroom = Chatroom.createChatroom(postId, tradeId, titleSuffix, MEMBER_ID, OTHER_MEMBER_ID);

        assertThat(chatroom.getPostId()).isEqualTo(postId);
        assertThat(chatroom.getTradeId()).isEqualTo(tradeId);
        assertThat(chatroom.getTitleSuffix()).isEqualTo(titleSuffix);
        assertThat(chatroom.getMembers()).hasSize(2);
        assertThat(chatroom.getMessages()).isEmpty();
        assertThat(chatroom.getCreatedAt()).isNotNull();
    }

    @Test
    void 채팅_메시지_추가() {
        Chatroom chatroom = createChatroom();
        String content = "안녕하세요";

        ChatMessage message = chatroom.addMessage(MEMBER_ID, content, TEXT);

        assertThat(chatroom.getMessages()).hasSize(1);
        assertThat(message.getContent()).isEqualTo(content);
    }

    @Test
    void 채팅_메시지_추가_이미지() {
        Chatroom chatroom = createChatroom();
        UUID senderId = UUID.randomUUID();

        ChatMessage message1 = chatroom.addMessage(senderId, "", IMAGE);
        ChatMessage message2 = chatroom.addMessage(senderId, null, IMAGE);

        assertThat(message1.getContent()).isEqualTo("사진");
        assertThat(message2.getContent()).isEqualTo("사진");
    }

    @Test
    void 채팅_시스템_메시지_추가() {
        Chatroom chatroom = createChatroom();
        String content = "거래가 시작되었습니다";

        ChatMessage message = chatroom.addSystemMessage(MEMBER_ID, content);

        assertThat(chatroom.getMessages()).hasSize(1);
        assertThat(message.getType().name()).isEqualTo("SYSTEM");
    }

    @Test
    void 채팅_메시지_읽음_처리() {
        Chatroom chatroom = createChatroom();
        UUID senderId = MEMBER_ID;
        UUID receiverId = OTHER_MEMBER_ID;

        addMessage(chatroom, 1L, senderId);
        addMessage(chatroom, 2L, senderId);
        addMessage(chatroom, 3L, receiverId);

        chatroom.markMessagesAsRead(receiverId);

        ChatroomMember receiver = chatroom.getMember(receiverId);
        assertThat(receiver.getLastReadMessageId()).isEqualTo(2L);
        assertThat(chatroom.countUnreadMessages(receiverId)).isZero();
    }

    @Test
    void 채팅방_나가기() {
        Chatroom chatroom = createChatroom();

        chatroom.exitMember(MEMBER_ID);

        assertThat(chatroom.isMemberExited(MEMBER_ID)).isTrue();
        assertThat(chatroom.isMemberExited(OTHER_MEMBER_ID)).isFalse();
    }

    @Test
    void 채팅방_재입장() {
        Chatroom chatroom = createChatroom();
        chatroom.exitMember(MEMBER_ID);

        chatroom.reEnterMember(MEMBER_ID);

        assertThat(chatroom.isMemberExited(MEMBER_ID)).isFalse();
    }

    @Test
    void 채팅방_회원_존재_확인() {
        Chatroom chatroom = createChatroom();

        assertThat(chatroom.hasMember(MEMBER_ID)).isTrue();
        assertThat(chatroom.hasMember(OTHER_MEMBER_ID)).isTrue();
        assertThat(chatroom.hasMember(UUID.randomUUID())).isFalse();
    }

    @Test
    void 채팅방_회원_조회() {
        Chatroom chatroom = createChatroom();

        ChatroomMember foundMember = chatroom.getMember(MEMBER_ID);

        assertThat(foundMember.getMemberId()).isEqualTo(MEMBER_ID);
    }

    @Test
    void 채팅_상대방_ID_조회() {
        Chatroom chatroom = createChatroom();

        UUID partnerId = chatroom.getPartnerId(MEMBER_ID);

        assertThat(partnerId).isEqualTo(OTHER_MEMBER_ID);
    }

    @Test
    void 채팅_메시지_조회() {
        Chatroom chatroom = createChatroom();
        UUID senderId = MEMBER_ID;

        LocalDateTime beforeTime = LocalDateTime.now().minusMinutes(10);
        chatroom.addMessage(senderId, "메시지1", TEXT);
        chatroom.addMessage(senderId, "메시지2", TEXT);
        LocalDateTime afterTime = LocalDateTime.now().plusMinutes(10);

        List<ChatMessage> messagesAfter = chatroom.getMessagesAfter(beforeTime);

        assertThat(messagesAfter).hasSize(2);
        assertThat(chatroom.getMessagesAfter(afterTime)).isEmpty();
    }

    @Test
    void 읽지않은_메시지_개수_조회() {
        Chatroom chatroom = createChatroom();
        UUID senderId = MEMBER_ID;
        UUID receiverId = OTHER_MEMBER_ID;

        addMessage(chatroom, 1L, senderId);
        addMessage(chatroom, 2L, senderId);

        chatroom.markMessagesAsRead(receiverId);

        addMessage(chatroom, 3L, receiverId);
        addMessage(chatroom, 4L, senderId);
        addMessage(chatroom, 5L, senderId);

        int unreadCount = chatroom.countUnreadMessages(receiverId);

        assertThat(unreadCount).isEqualTo(2);
    }

    @Test
    void 읽지않은_메시지_개수_조회_시_시스템_메시지는_포함되지_않음() {
        Chatroom chatroom = createChatroom();
        UUID senderId = MEMBER_ID;
        UUID receiverId = OTHER_MEMBER_ID;

        chatroom.addMessage(senderId, "메시지", TEXT);
        chatroom.addMessage(senderId, "메시지", TEXT);
        chatroom.addMessage(receiverId, "상대방 메시지", TEXT); // 상대방 메시지 포함 X
        chatroom.addSystemMessage(senderId, "시스템 메시지"); // 시스템 메시지 포함 X

        int unreadCount = chatroom.countUnreadMessages(receiverId);

        assertThat(unreadCount).isEqualTo(2);
    }

    @Test
    void 모든_채팅_회원_재입장() {
        UUID member1 = MEMBER_ID;
        UUID member2 = OTHER_MEMBER_ID;
        Chatroom chatroom = Chatroom.createChatroom(1L, 1L, "제목", member1, member2);

        chatroom.exitMember(member1);
        chatroom.exitMember(member2);
        chatroom.reEnterAllMembers();

        assertThat(chatroom.isMemberExited(member1)).isFalse();
        assertThat(chatroom.isMemberExited(member2)).isFalse();
    }
}
