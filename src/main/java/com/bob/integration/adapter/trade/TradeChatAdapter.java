package com.bob.integration.adapter.trade;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.chat.application.dto.command.CreateChatroomCommand;
import com.bob.core.chat.application.port.in.ChatroomCreator;
import com.bob.core.chat.domain.Chatroom;
import com.bob.core.trade.application.port.out.TradeChatPort;

@Component
@RequiredArgsConstructor
public class TradeChatAdapter implements TradeChatPort {

    private final ChatroomCreator chatRoomCreator;

    @Override
    public Long create(Long postId, Long tradeId, UUID buyerId, boolean isFar) {
        CreateChatroomCommand command = new CreateChatroomCommand(postId, tradeId, buyerId, isFar);

        Chatroom chatroom = chatRoomCreator.create(command);

        return chatroom.getId();
    }
}
