package com.bob.core.chat.adapter.api;

import static com.bob.support.fixture.chat.domain.ChatroomFixture.createChatroom;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.core.chat.adapter.api.request.CreateChatMessageRequest;
import com.bob.core.chat.adapter.api.response.ChatMessageSendResponse;
import com.bob.core.chat.adapter.api.response.ChatroomDetailResponse;
import com.bob.core.chat.adapter.api.response.UnreadMessageCountResponse;
import com.bob.core.chat.application.dto.result.ChatMessageSummary;
import com.bob.core.chat.application.dto.result.ChatroomSummary;
import com.bob.core.chat.domain.Chatroom;
import com.bob.core.chat.domain.repository.ChatroomRepository;
import com.bob.core.chat.domain.type.ChatMessageType;
import com.bob.core.trade.application.port.in.TradeReader;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;

@RequiredArgsConstructor
@DisplayName("채팅 API 테스트")
@BobApiTest
class ChatApiTest {

    final MockMvcTester mvcTester;

    final ChatroomRepository chatroomRepository;

    final ObjectMapper objectMapper;

    @MockitoBean
    TradeReader tradeReader;

    @Test
    void 채팅_메시지_전송() throws Exception {
        Chatroom chatroom = chatroomRepository.save(createChatroom());
        CreateChatMessageRequest request = new CreateChatMessageRequest("테스트 메시지", null);

        setAuthentication();

        MvcTestResult result = mvcTester.post()
            .uri("/chatrooms/{chatroomId}/messages", chatroom.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .exchange();

        assertThat(result).hasStatus2xxSuccessful();

        ChatMessageSendResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(), ChatMessageSendResponse.class
        );

        assertThat(response.isRead()).isFalse();
    }

    @Test
    void 채팅방_목록_조회() throws Exception {
        Chatroom chatroom1 = createChatroom();
        Chatroom chatroom2 = createChatroom();
        chatroomRepository.saveAll(List.of(chatroom1, chatroom2));

        setAuthentication();

        MvcTestResult result = mvcTester.get()
            .uri("/chatrooms")
            .exchange();

        assertThat(result).hasStatusOk();

        List<ChatroomSummary> response = objectMapper.readValue(
            result.getResponse().getContentAsString(), new TypeReference<>() {
            }
        );

        assertThat(response).hasSize(2);
    }

    @Test
    void 읽지않은_메시지_수_조회() throws Exception {
        Chatroom chatroom = createChatroom();
        chatroom.addMessage(OTHER_MEMBER_ID, "읽지 않은 메시지1", ChatMessageType.TEXT);
        chatroom.addMessage(OTHER_MEMBER_ID, "읽지 않은 메시지2", ChatMessageType.TEXT);
        chatroom.addMessage(MEMBER_ID, "내가 보낸 메시지", ChatMessageType.TEXT);
        chatroomRepository.save(chatroom);

        setAuthentication();

        MvcTestResult result = mvcTester.get()
            .uri("/chatrooms/messages/unread")
            .exchange();

        assertThat(result).hasStatusOk();

        UnreadMessageCountResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(), UnreadMessageCountResponse.class
        );

        assertThat(response.unreadCount()).isEqualTo(2);
    }

    @Test
    void 채팅방_상세_조회() throws Exception {
        Chatroom chatroom = createChatroom();
        chatroomRepository.save(chatroom);

        given(tradeReader.readTradeStatus(any())).willReturn("ACCEPTED");

        setAuthentication();

        MvcTestResult result = mvcTester.get()
            .uri("/chatrooms/{chatroomId}", chatroom.getId())
            .exchange();

        assertThat(result).hasStatusOk();

        ChatroomDetailResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(), ChatroomDetailResponse.class
        );

        assertThat(response.id()).isEqualTo(chatroom.getId());
        assertThat(response.trade().status()).isEqualTo("ACCEPTED");
    }

    @Test
    void 채팅_메시지_목록_조회() throws Exception {
        Chatroom chatroom = createChatroom();
        chatroom.addMessage(MEMBER_ID, "메시지1", ChatMessageType.TEXT);
        chatroom.addMessage(OTHER_MEMBER_ID, "메시지2", ChatMessageType.TEXT);
        chatroomRepository.save(chatroom);

        setAuthentication();

        MvcTestResult result = mvcTester.get()
            .uri("/chatrooms/{chatroomId}/messages", chatroom.getId())
            .exchange();

        assertThat(result).hasStatusOk();

        List<ChatMessageSummary> response = objectMapper.readValue(
            result.getResponse().getContentAsString(), new TypeReference<>() {
            }
        );

        assertThat(response).hasSize(2);
    }

    @Test
    void 채팅방_나가기() {
        Chatroom chatroom = createChatroom();
        chatroomRepository.save(chatroom);

        setAuthentication();

        MvcTestResult result = mvcTester.patch()
            .uri("/chatrooms/{chatroomId}", chatroom.getId())
            .exchange();

        assertThat(result).hasStatusOk();

        Chatroom updated = chatroomRepository.findById(chatroom.getId()).orElseThrow();
        assertThat(updated.isMemberExited(MEMBER_ID)).isTrue();
    }

    void setAuthentication() {
        MemberDetails principal = new MemberDetails(MEMBER_ID, true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
