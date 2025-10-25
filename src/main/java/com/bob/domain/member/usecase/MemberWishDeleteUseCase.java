package com.bob.domain.member.usecase;

import com.bob.domain.member.service.dto.command.DeleteMemberWishCommand;

public interface MemberWishDeleteUseCase {

  void deleteWishProcess(DeleteMemberWishCommand command);
}
