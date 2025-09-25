package com.bob.web.member.adapter.in;

import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.usecase.MemberBookWriteUseCase;
import com.bob.domain.member.usecase.MemberReadUseCase;
import com.bob.domain.post.service.port.out.PostMemberPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostMemberAdapter implements PostMemberPort {

  private final MemberReadUseCase readUseCase;

  private final MemberBookWriteUseCase bookWriteUseCase;

  @Override
  public MemberProfileResponse readPostMemberSummary(UUID memberId) {
    return readUseCase.readProfileProcess(ReadProfileQuery.of(memberId, false));
  }

  @Override
  public void createMemberBook(RegisterMemberBookCommand command) {
    bookWriteUseCase.registerMemberBookProcess(command);
  }
}
