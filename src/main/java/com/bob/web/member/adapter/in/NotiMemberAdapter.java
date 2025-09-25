package com.bob.web.member.adapter.in;

import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.usecase.MemberReadUseCase;
import com.bob.domain.notification.service.port.NotiMemberPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotiMemberAdapter implements NotiMemberPort {

  private final MemberReadUseCase readUseCase;

  @Override
  public MemberProfileResponse readNotiMemberProfile(UUID memberId) {
    return readUseCase.readProfileProcess(ReadProfileQuery.of(memberId, false));
  }
}
