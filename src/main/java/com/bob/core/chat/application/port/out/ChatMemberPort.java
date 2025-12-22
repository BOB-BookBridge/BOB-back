package com.bob.core.chat.application.port.out;

import java.util.UUID;

import com.bob.core.chat.application.port.result.ChatMember;

public interface ChatMemberPort {

    ChatMember read(UUID memberId);
}
