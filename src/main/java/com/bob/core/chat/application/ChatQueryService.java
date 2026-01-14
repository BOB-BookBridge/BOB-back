package com.bob.core.chat.application;

import static com.bob.core.chat.application.dto.result.ChatMessageSummary.of;
import static com.bob.global.exception.response.ApplicationError.CHATROOM_ACCESS_DENIED;
import static com.bob.global.utils.stream.StreamUtils.sortByDesc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.chat.application.dto.query.ReadChatMessagesQuery;
import com.bob.core.chat.application.dto.query.ReadChatroomByPostAndMemberQuery;
import com.bob.core.chat.application.dto.query.ReadChatroomDetailQuery;
import com.bob.core.chat.application.dto.query.ReadChatroomSummariesQuery;
import com.bob.core.chat.application.dto.query.ReadUnreadMessageCountQuery;
import com.bob.core.chat.application.dto.query.ValidateParticipantQuery;
import com.bob.core.chat.application.dto.result.ChatMessageSummary;
import com.bob.core.chat.application.dto.result.ChatroomDetail;
import com.bob.core.chat.application.dto.result.ChatroomSummary;
import com.bob.core.chat.application.port.in.ChatMessageReader;
import com.bob.core.chat.application.port.in.ChatroomReader;
import com.bob.core.chat.application.port.out.ChatFilePort;
import com.bob.core.chat.application.port.out.ChatMemberPort;
import com.bob.core.chat.application.port.out.ChatPostPort;
import com.bob.core.chat.application.port.out.ChatTradePort;
import com.bob.core.chat.application.port.result.ChatFile;
import com.bob.core.chat.application.port.result.ChatMember;
import com.bob.core.chat.application.port.result.ChatPost;
import com.bob.core.chat.domain.ChatMessage;
import com.bob.core.chat.domain.Chatroom;
import com.bob.core.chat.domain.ChatroomMember;
import com.bob.core.chat.domain.repository.ChatroomRepository;
import com.bob.global.exception.exceptions.ApplicationException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatQueryService implements ChatroomReader, ChatMessageReader {

    private final ChatroomRepository chatRoomRepository;

    private final ChatMemberPort memberPort;
    private final ChatPostPort postPort;
    private final ChatFilePort filePort;
    private final ChatTradePort tradePort;

    @Override
    public Chatroom read(Long id) {
        return chatRoomRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. id : " + id));
    }

    @Override
    public Chatroom readByMessageId(Long messageId) {
        return chatRoomRepository.findByMessageId(messageId)
            .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다. id : " + messageId));
    }

    @Override
    public Optional<Chatroom> readByPostAndBuyer(ReadChatroomByPostAndMemberQuery query) {
        return chatRoomRepository.findByPostAndBuyer(query.postId(), query.buyerId());
    }

    @Override
    public List<ChatroomSummary> readChatRoomSummaries(ReadChatroomSummariesQuery query) {
        List<ChatroomSummary> responses = chatRoomRepository.findAllByMemberId(query.memberId()).stream()
            .map(chatRoom -> convertToChatRoomSummary(query, chatRoom))
            .toList();

        return sortByDesc(responses, ChatroomSummary::lastMessageAt);
    }

    @Override
    public List<ChatMessageSummary> readMessages(Long id, ReadChatMessagesQuery query) {
        Chatroom chatroom = read(id);
        verifyParticipating(chatroom, query.memberId());

        ChatroomMember member = chatroom.getMember(query.memberId());
        List<ChatMessage> messages = chatroom.getMessagesAfter(member.getEnteredAt().minusSeconds(1));
        UUID partnerId = chatroom.getPartnerId(query.memberId());
        ChatroomMember partner = chatroom.getMember(partnerId);

        return messages.stream()
            .map(message -> of(message, query.memberId(), readChatFiles(message), partner.getLastReadMessageId()))
            .toList();
    }

    @Override
    public ChatroomDetail readChatRoomDetail(Long id, ReadChatroomDetailQuery query) {
        Chatroom chatRoom = read(id);
        verifyParticipating(chatRoom, query.memberId());

        UUID partnerId = chatRoom.getPartnerId(query.memberId());

        ChatPost post = postPort.read(chatRoom.getPostId());

        ChatMember member = memberPort.read(partnerId);

        String tradeStatus = tradePort.readTradeStatus(chatRoom.getTradeId());

        return ChatroomDetail.of(chatRoom, post, member, tradeStatus);
    }

    @Override
    public int countUnreadMessagesByMember(ReadUnreadMessageCountQuery query) {
        return chatRoomRepository.findAllByMemberId(query.memberId()).stream()
            .mapToInt(chatroom -> chatroom.countUnreadMessages(query.memberId()))
            .sum();
    }

    @Override
    public void validateParticipant(Long id, ValidateParticipantQuery query) {
        Chatroom chatroom = read(id);

        verifyParticipating(chatroom, query.memberId());
    }

    private ChatroomSummary convertToChatRoomSummary(ReadChatroomSummariesQuery query, Chatroom chatRoom) {
        UUID partnerId = chatRoom.getPartnerId(query.memberId());
        ChatMember memberSummary = memberPort.read(partnerId);

        ChatPost postSummary = postPort.read(chatRoom.getPostId());

        int unreadCount = chatRoom.countUnreadMessages(query.memberId());

        return ChatroomSummary.of(chatRoom, memberSummary, postSummary, unreadCount);
    }

    private List<ChatFile> readChatFiles(ChatMessage message) {
        return !message.getType().hasFile() ? List.of() : filePort.read(message.getId());
    }

    private void verifyParticipating(Chatroom chatroom, UUID memberId) {
        if (!chatroom.hasMember(memberId) || chatroom.isMemberExited(memberId))
            throw new ApplicationException(CHATROOM_ACCESS_DENIED);
    }
}
