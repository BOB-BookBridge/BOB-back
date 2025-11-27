package com.bob.core.application.chat;

import static com.bob.core.application.chat.dto.result.ChatMessageSummary.of;
import static com.bob.global.utils.stream.StreamUtils.sortByDesc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.chat.dto.query.ReadChatMessagesQuery;
import com.bob.core.application.chat.dto.query.ReadChatroomByPostAndMemberQuery;
import com.bob.core.application.chat.dto.query.ReadChatroomDetailQuery;
import com.bob.core.application.chat.dto.query.ReadChatroomSummariesQuery;
import com.bob.core.application.chat.dto.query.ReadUnreadMessageCountQuery;
import com.bob.core.application.chat.dto.query.ValidateParticipantQuery;
import com.bob.core.application.chat.dto.result.ChatMessageSummary;
import com.bob.core.application.chat.dto.result.ChatroomDetail;
import com.bob.core.application.chat.dto.result.ChatroomSummary;
import com.bob.core.application.chat.port.in.ChatMessageReader;
import com.bob.core.application.chat.port.in.ChatroomReader;
import com.bob.core.application.chat.port.out.ChatFilePort;
import com.bob.core.application.chat.port.out.ChatMemberPort;
import com.bob.core.application.chat.port.out.ChatPostPort;
import com.bob.core.application.chat.port.result.ChatFile;
import com.bob.core.application.chat.port.result.ChatMember;
import com.bob.core.application.chat.port.result.ChatPost;
import com.bob.core.domain.chat.ChatMessage;
import com.bob.core.domain.chat.Chatroom;
import com.bob.core.domain.chat.ChatroomMember;
import com.bob.core.domain.chat.repository.ChatroomRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatQueryService implements ChatroomReader, ChatMessageReader {

    private final ChatroomRepository chatRoomRepository;

    private final ChatMemberPort memberPort;
    private final ChatPostPort postPort;
    private final ChatFilePort filePort;

    @Override
    public Chatroom read(Long id) {
        return chatRoomRepository.findById(id)
            .orElseThrow(() -> new ApplicationException(ApplicationError.NOT_EXISTS_CHAT_ROOM));
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
        List<ChatMessage> messages = chatroom.getMessagesAfter(member.getEnteredAt());
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

        return ChatroomDetail.of(chatRoom, post, member);
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
            throw new ApplicationException(ApplicationError.NOT_PARTICIPATED_CHAT_ROOM);
    }
}
