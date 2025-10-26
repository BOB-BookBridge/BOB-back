package com.bob.web.member.adapter.in;

import com.bob.domain.member.service.dto.command.ChangeMemberBookUsageCommand;
import com.bob.domain.member.service.dto.command.FreeMemberBookUsageByUsageIdCommand;
import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.usecase.MemberBookModifyUseCase;
import com.bob.domain.member.usecase.MemberBookWriteUseCase;
import com.bob.domain.member.usecase.MemberReadUseCase;
import com.bob.domain.post.service.port.out.PostMemberPort;
import com.bob.domain.post.service.port.view.PostMemberView;
import com.bob.domain.post.service.port.view.PostMemberWishView;
import com.bob.domain.post.service.port.view.PostMemberWishesView;
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
  public PostMemberView readPostMemberSummary(UUID memberId) {
    MemberProfileResponse response = readUseCase.readProfileProcess(ReadProfileQuery.of(memberId, false));
    List<PostMemberWishView> wishes = response.wishes().stream()
        .map((wish) -> PostMemberWishView.of(wish.id(), wish.title(), wish.author(), wish.cover()))
        .toList();

    return PostMemberView.of(
        response.memberId(),
        response.nickname(),
        response.area().emdId(),
        response.profileImageUrl(),
        response.interests(),
        PostMemberWishesView.from(wishes)
    );
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
    bookModifyUseCase.freeMemberBookUsageByUsageIdProcess(FreeMemberBookUsageByUsageIdCommand.of(usageId));
  }
}
