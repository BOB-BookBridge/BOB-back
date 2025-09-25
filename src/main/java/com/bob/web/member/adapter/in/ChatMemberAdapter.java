package com.bob.web.member.adapter.in;

import com.bob.domain.chat.service.port.out.ChatMemberPort;
import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.usecase.MemberReadUseCase;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatMemberAdapter implements ChatMemberPort {

  private final MemberReadUseCase readUseCase;

  @Override
  public MemberProfileResponse readChatMemberProfile(UUID memberId) {
    return readUseCase.readProfileProcess(ReadProfileQuery.of(memberId, false));
  }
}
