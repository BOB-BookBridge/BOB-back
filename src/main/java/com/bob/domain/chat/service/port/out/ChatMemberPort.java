package com.bob.domain.chat.service.port.out;

import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import java.util.UUID;

public interface ChatMemberPort {

  MemberProfileResponse readChatMemberProfile(UUID memberId);
}
