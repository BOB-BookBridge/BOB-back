package com.bob.web.member.adapter.in;

import com.bob.domain.member.service.dto.command.ChangeMemberBookUsageCommand;
import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.member.service.dto.command.RemoveMemberBookUsageCommand;
import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.usecase.MemberBookModifyUseCase;
import com.bob.domain.member.usecase.MemberBookWriteUseCase;
import com.bob.domain.member.usecase.MemberReadUseCase;
import com.bob.domain.post.service.port.out.PostMemberPort;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostMemberAdapter implements PostMemberPort {

  private final MemberReadUseCase readUseCase;

  private final MemberBookWriteUseCase bookWriteUseCase;
  private final MemberBookModifyUseCase bookModifyUseCase;

  @Override
  public MemberProfileResponse readPostMemberSummary(UUID memberId) {
    return readUseCase.readProfileProcess(ReadProfileQuery.of(memberId, false));
  }

  @Override
  public Long createMemberBook(RegisterMemberBookCommand command) {
    return bookWriteUseCase.registerMemberBookProcess(command);
  }

  @Override
  public void changeMemberBookUsage(UUID memberId, Long usageId, Long bookId) {
    bookModifyUseCase.changeMemberBookUsageProcess(ChangeMemberBookUsageCommand.of(memberId, usageId, List.of(bookId), false));
  }

  @Override
  public void removeMemberBookUsage(Long usageId) {
    bookModifyUseCase.removeMemberBookUsageProcess(RemoveMemberBookUsageCommand.of(usageId));
  }
}
