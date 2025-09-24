package com.bob.domain.post.service.port.out;

import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import java.util.UUID;

public interface PostMemberPort {

  MemberProfileResponse readPostMemberSummary(UUID memberId);

  void createMemberBook(RegisterMemberBookCommand command);
}
