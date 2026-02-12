package com.bob.core.chat.application.event;

import lombok.RequiredArgsConstructor;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import com.bob.core.chat.application.dto.command.DeactivateChatroomCommand;
import com.bob.core.chat.application.port.in.ChatroomModifier;
import com.bob.core.chat.application.port.in.ChatroomReader;
import com.bob.core.chat.domain.Chatroom;
import com.bob.core.post.application.dto.command.ChangeMemberPostStatusCommand;
import com.bob.core.post.domain.status.Status;
import com.bob.core.report.event.ReportChatProcessedEvent;

@Component
@RequiredArgsConstructor
public class ChatroomEventHandler {

    private final ChatroomModifier chatroomModifier;

    @ApplicationModuleListener
    public void handleChatReportProcessed(ReportChatProcessedEvent event) {
        var command = new DeactivateChatroomCommand(event.chatMessageId());

        chatroomModifier.deactivate(command);
    }
}
