package com.bob.core.chat.application;

import static com.bob.core.chat.domain.Chatroom.createChatroom;
import static com.bob.global.event.application.dto.type.NotiEventType.CHAT;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.chat.application.dto.command.CreateChatroomCommand;
import com.bob.core.chat.application.dto.command.ExitChatroomCommand;
import com.bob.core.chat.application.dto.command.JoinChatroomCommand;
import com.bob.core.chat.application.dto.query.ReadChatroomByPostAndMemberQuery;
import com.bob.core.chat.application.port.in.ChatroomCreator;
import com.bob.core.chat.application.port.in.ChatroomModifier;
import com.bob.core.chat.application.port.in.ChatroomReader;
import com.bob.core.chat.application.port.out.ChatPostPort;
import com.bob.core.chat.application.port.result.ChatPost;
import com.bob.core.chat.domain.Chatroom;
import com.bob.core.chat.domain.repository.ChatroomRepository;
import com.bob.global.event.application.dto.NotificationEvent;

@RequiredArgsConstructor
@Service
@Transactional
public class ChatroomCommandService implements ChatroomCreator, ChatroomModifier {

    private final ChatroomRepository chatroomRepository;

    private final ChatroomReader chatroomReader;

    private final ChatPostPort postPort;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Chatroom create(CreateChatroomCommand command) {
        ChatPost post = postPort.read(command.postId());
        return chatroomReader.readByPostAndBuyer(new ReadChatroomByPostAndMemberQuery(post.id(), command.buyerId()))
            .map(chatroom -> {
                chatroom.reEnterAllMembers();
                return chatroomRepository.save(chatroom);
            })
            .orElseGet(() -> createNewChatroom(command, post));
    }

    private Chatroom createNewChatroom(CreateChatroomCommand command, ChatPost post) {
        Chatroom chatroom
            = createChatroom(post.id(), command.tradeId(), post.title(), post.sellerId(), command.buyerId());

        if (command.isFar())
            chatroom.addSystemMessage(command.buyerId(), "거리가 먼 사용자와의 채팅입니다.");

        return chatroomRepository.save(chatroom);
    }

    @Override
    public void join(Long chatroomId, JoinChatroomCommand command) {
        Chatroom chatroom = chatroomReader.read(chatroomId);
        chatroom.markMessagesAsRead(command.memberId());

        UUID partnerId = chatroom.getPartnerId(command.memberId());
        publishSystemChatEvent(chatroomId, command.memberId(), partnerId);
    }

    @Override
    public void exit(Long chatroomId, ExitChatroomCommand command) {
        Chatroom chatroom = chatroomReader.read(chatroomId);
        chatroom.exitMember(command.memberId());

        chatroom.markMessagesAsRead(command.memberId());
    }

    private void publishSystemChatEvent(Long chatRoomId, UUID senderId, UUID receiverId) {
        eventPublisher.publishEvent(
            NotificationEvent.toSystemEvent(CHAT, chatRoomId.toString(), "READ_ACK", senderId, receiverId, null)
        );
    }
}
