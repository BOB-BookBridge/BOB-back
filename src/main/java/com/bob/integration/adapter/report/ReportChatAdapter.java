package com.bob.integration.adapter.report;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.chat.application.port.in.ChatroomReader;
import com.bob.core.chat.domain.Chatroom;
import com.bob.core.report.application.port.out.ReportChatPort;
import com.bob.shared.entity.AbstractEntity;

@Component
@RequiredArgsConstructor
public class ReportChatAdapter implements ReportChatPort {

    private final ChatroomReader chatroomReader;

    @Override
    public List<Long> readMessageIds(Long messageId) {
        Chatroom chatroom = chatroomReader.readByMessageId(messageId);

        return chatroom.getMessages().stream()
            .map(AbstractEntity::getId)
            .toList();
    }
}
