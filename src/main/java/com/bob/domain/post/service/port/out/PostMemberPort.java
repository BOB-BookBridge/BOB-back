package com.bob.domain.post.service.port.out;

import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.post.service.port.view.PostMemberView;
import java.util.UUID;

public interface PostMemberPort {

  PostMemberView readPostMemberSummary(UUID memberId);

  Long createMemberBook(RegisterMemberBookCommand command);

  void changeMemberBookUsage(UUID memberId, Long usageId, Long bookId);

  void removeMemberBookUsage(Long usageId);
}
