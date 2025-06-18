package com.bob.domain.member.adapter;

import com.bob.domain.member.service.MemberService;
import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.post.service.dto.response.PostMemberSummaryResponse;
import com.bob.domain.post.service.port.PostMemberPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostMemberAdapter implements PostMemberPort {

  private final MemberService memberService;

  @Override
  public PostMemberSummaryResponse readPostMemberSummary(UUID memberId) {
    MemberProfileResponse response = memberService.readProfileProcess(ReadProfileQuery.of(memberId));
    return PostMemberSummaryResponse.of(response.nickname(), response.profileImageUrl());
  }
}
