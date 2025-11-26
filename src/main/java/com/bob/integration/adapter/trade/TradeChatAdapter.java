package com.bob.integration.adapter.trade;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.chat.dto.command.CreateChatroomCommand;
import com.bob.core.application.chat.port.in.ChatroomCreator;
import com.bob.core.application.trade.port.out.TradeChatPort;
import com.bob.core.domain.chat.Chatroom;

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
