package com.bob.core.chat.application;

import static com.bob.core.chat.domain.ChatMessage.resolveMessageType;
import static com.bob.global.event.sse.repository.chat.ChatEmitterKey.of;
import static com.bob.global.exception.response.ApplicationError.CHATROOM_ACCESS_DENIED;
import static com.bob.global.exception.response.ApplicationError.CHATROOM_DEACTIVATED;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.chat.application.dto.command.CreateMessageCommand;
import com.bob.core.chat.application.dto.command.CreateSystemMessageCommand;
import com.bob.core.chat.application.dto.query.ReadChatroomByPostAndMemberQuery;
import com.bob.core.chat.application.dto.result.ChatMessageCreationResult;
import com.bob.core.chat.application.port.in.ChatMessageCreator;
import com.bob.core.chat.application.port.in.ChatroomReader;
import com.bob.core.chat.application.port.out.ChatFilePort;
import com.bob.core.chat.domain.ChatMessage;
import com.bob.core.chat.domain.Chatroom;
import com.bob.core.chat.domain.ChatroomMember;
import com.bob.core.chat.domain.repository.ChatroomRepository;
import com.bob.core.chat.domain.type.ChatMessageType;
import com.bob.core.chat.event.ChatMessageSentEvent;
import com.bob.global.event.sse.manager.EmitterManager;
import com.bob.global.event.sse.manager.type.EmitterType;
import com.bob.global.exception.exceptions.ApplicationException;

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
        verifyActive(chatroom);
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
        publishMessageSentEvent(chatroom.getId(), command, message, partnerId);

        return ChatMessageCreationResult.of(message, partner.getLastReadMessageId());
    }

    @Override
    public ChatMessage createSystemChatMessage(CreateSystemMessageCommand command) {
        Long postId = Long.valueOf(command.refId());
        return chatroomReader.readByPostAndBuyer(new ReadChatroomByPostAndMemberQuery(postId, command.partnerId()))
            .map(chatroom -> {
                ChatMessage message = chatroom.addSystemMessage(command.senderId(), command.body());
                chatroomRepository.flush();

                publishSystemMessageSentEvent(chatroom.getId(), command.senderId(), command.partnerId(),
                    message.getContent());

                return message;
            })
            .orElse(null);
    }

    private void verifyActive(Chatroom chatroom) {
        if (!chatroom.isActive())
            throw new ApplicationException(CHATROOM_DEACTIVATED);
    }

    private void verifyParticipating(Chatroom chatroom, UUID memberId) {
        if (!chatroom.hasMember(memberId) || chatroom.isMemberExited(memberId))
            throw new ApplicationException(CHATROOM_ACCESS_DENIED);
    }

    private void publishMessageSentEvent(Long id, CreateMessageCommand command, ChatMessage message, UUID partnerId) {
        ChatMessageSentEvent event = ChatMessageSentEvent.of(
            id, message.getId().toString(), command.memberId(), partnerId, message.getContent(), command.fileNames(),
            message.getType() != ChatMessageType.TEXT
        );

        eventPublisher.publishEvent(event);
    }

    private void publishSystemMessageSentEvent(Long id, UUID senderId, UUID receiverId, String content) {
        ChatMessageSentEvent event = ChatMessageSentEvent.toSystemEvent(id, senderId, receiverId, content);

        eventPublisher.publishEvent(event);
    }

    private void imageMapping(List<String> fileNames, Long messageId) {
        if (fileNames == null || fileNames.isEmpty())
            return;

        filePort.mappingReferenceId(fileNames, String.valueOf(messageId));
    }
}
