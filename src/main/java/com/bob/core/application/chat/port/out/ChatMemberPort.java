package com.bob.core.application.chat.port.out;

import java.util.UUID;

import com.bob.core.application.chat.port.result.ChatMember;

public interface ChatMemberPort {

    ChatMember read(UUID memberId);
}
