package com.bob.core.application.chat;

import static com.bob.core.domain.chat.ChatMessage.resolveMessageType;
import static com.bob.global.event.application.dto.type.NotiEventType.CHAT;
import static com.bob.global.event.sse.repository.chat.ChatEmitterKey.of;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.chat.dto.command.CreateMessageCommand;
import com.bob.core.application.chat.dto.command.CreateSystemMessageCommand;
import com.bob.core.application.chat.dto.query.ReadChatroomByPostAndMemberQuery;
import com.bob.core.application.chat.dto.result.ChatMessageCreationResult;
import com.bob.core.application.chat.port.in.ChatMessageCreator;
import com.bob.core.application.chat.port.in.ChatroomReader;
import com.bob.core.application.chat.port.out.ChatFilePort;
import com.bob.core.domain.chat.ChatMessage;
import com.bob.core.domain.chat.Chatroom;
import com.bob.core.domain.chat.ChatroomMember;
import com.bob.core.domain.chat.repository.ChatroomRepository;
import com.bob.core.domain.chat.type.ChatMessageType;
import com.bob.global.event.application.dto.NotificationEvent;
import com.bob.global.event.sse.manager.EmitterManager;
import com.bob.global.event.sse.manager.type.EmitterType;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatMessageCommandService implements ChatMessageCreator {

    private final ChatroomRepository chatroomRepository;
    private final ChatroomReader chatroomReader;

    private final ChatFilePort filePort;

    private final EmitterManager emitterManager;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ChatMessageCreationResult createChatMessage(Long chatroomId, CreateMessageCommand command) {
        Chatroom chatroom = chatroomReader.read(chatroomId);
        verifyParticipating(chatroom, command.memberId());

        UUID partnerId = chatroom.getPartnerId(command.memberId());
        chatroom.reEnterMember(partnerId);

        ChatMessageType type = resolveMessageType(command.fileNames(), command.content());
        ChatMessage message = chatroom.addMessage(command.memberId(), command.content(), type);
        chatroomRepository.flush();

        ChatroomMember partner = chatroom.getMember(partnerId);
        if (emitterManager.isExistConnection(EmitterType.CHAT, of(chatroomId, partnerId)))
            partner.updateLastReadMessage(message.getId());

        imageMapping(command.fileNames(), message.getId());
        publishChatMessageEvent(chatroom.getId(), command, message, partnerId);

        return ChatMessageCreationResult.of(message, partner.getLastReadMessageId());
    }

    @Override
    public ChatMessage createSystemChatMessage(CreateSystemMessageCommand command) {
        Long postId = Long.valueOf(command.refId());
        return chatroomReader.readByPostAndBuyer(new ReadChatroomByPostAndMemberQuery(postId, command.partnerId()))
            .map(chatroom -> {
                ChatMessage message = chatroom.addSystemMessage(command.senderId(), command.body());
                chatroomRepository.flush();

                publishSystemChatEvent(chatroom.getId(), command.senderId(), command.partnerId(), message.getContent());

                return message;
            })
            .orElse(null);
    }

    private void verifyParticipating(Chatroom chatroom, UUID memberId) {
        if (!chatroom.hasMember(memberId) || chatroom.isMemberExited(memberId))
            throw new ApplicationException(ApplicationError.NOT_PARTICIPATED_CHAT_ROOM);
    }

    private void publishChatMessageEvent(Long id, CreateMessageCommand command, ChatMessage message, UUID partnerId) {
        eventPublisher.publishEvent(
            NotificationEvent.of(CHAT, id.toString(), message.getId().toString(), command.memberId(), partnerId,
                message.getContent(), command.fileNames(), message.getType() != ChatMessageType.TEXT
            ));
    }

    private void publishSystemChatEvent(Long id, UUID senderId, UUID receiverId, String content) {
        eventPublisher.publishEvent(
            NotificationEvent.toSystemEvent(CHAT, id.toString(), "SYSTEM", senderId, receiverId, content)
        );
    }

    private void imageMapping(List<String> fileNames, Long messageId) {
        if (fileNames == null || fileNames.isEmpty())
            return;

        filePort.mappingReferenceId(fileNames, String.valueOf(messageId));
    }
}
