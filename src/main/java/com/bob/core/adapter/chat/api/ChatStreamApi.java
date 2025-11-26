package com.bob.core.adapter.chat.api;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bob.core.adapter.common.AuthenticationId;
import com.bob.core.application.chat.dto.command.JoinChatroomCommand;
import com.bob.core.application.chat.dto.query.ValidateParticipantQuery;
import com.bob.core.application.chat.port.in.ChatroomModifier;
import com.bob.core.application.chat.port.in.ChatroomReader;
import com.bob.global.event.sse.manager.EmitterManager;

@RequiredArgsConstructor
@RequestMapping("/chatrooms")
@RestController
public class ChatStreamApi {

    private final ChatroomReader chatRoomReader;
    private final ChatroomModifier chatRoomModifier;

    private final EmitterManager emitterManager;

    @GetMapping("/{chatroomId}/subscribe")
    public SseEmitter subscribe(@PathVariable Long chatroomId, @AuthenticationId UUID memberId) {
        ValidateParticipantQuery query = new ValidateParticipantQuery(memberId);
        chatRoomReader.validateParticipant(chatroomId, query);

        JoinChatroomCommand command = new JoinChatroomCommand(memberId);
        chatRoomModifier.join(chatroomId, command);

        return emitterManager.subscribeToChat(chatroomId, memberId);
    }
}
