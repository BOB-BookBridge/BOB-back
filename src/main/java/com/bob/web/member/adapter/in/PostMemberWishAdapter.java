package com.bob.web.member.adapter.in;

import com.bob.domain.member.service.dto.query.ReadMemberWishesQuery;
import com.bob.domain.member.service.dto.response.MemberWishesResult;
import com.bob.domain.member.usecase.MemberWishReadUseCase;
import com.bob.domain.post.service.port.out.PostMemberWishPort;
import com.bob.domain.post.service.port.view.PostMemberWishView;
import com.bob.domain.post.service.port.view.PostMemberWishesView;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostMemberWishAdapter implements PostMemberWishPort {

  private final MemberWishReadUseCase readUseCase;

  @Override
  public PostMemberWishesView read(UUID memberId) {
    ReadMemberWishesQuery query = ReadMemberWishesQuery.of(memberId);
    MemberWishesResult result = readUseCase.readWishesProcess(query);
    return PostMemberWishesView.from(result.wishes().stream()
        .map((wish) -> PostMemberWishView.of(wish.title(), wish.author(), wish.cover()))
        .toList());
  }

  @Override
  public boolean exists(UUID memberID) {
    ReadMemberWishesQuery query = ReadMemberWishesQuery.of(memberID);
    MemberWishesResult result = readUseCase.readWishesProcess(query);
    return !result.wishes().isEmpty();
  }
}
