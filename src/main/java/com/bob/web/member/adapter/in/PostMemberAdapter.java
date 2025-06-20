package com.bob.web.member.adapter.in;

import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.usecase.MemberReadUseCase;
import com.bob.domain.post.service.dto.response.PostMemberSummaryResponse;
import com.bob.domain.post.service.port.out.PostMemberPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostMemberAdapter implements PostMemberPort {

  private final MemberReadUseCase readUseCase;

  @Override
  public PostMemberSummaryResponse readPostMemberSummary(UUID memberId) {
    MemberProfileResponse response = readUseCase.readProfileProcess(ReadProfileQuery.of(memberId));
    return PostMemberSummaryResponse.of(response.nickname(), response.profileImageUrl());
  }
}
