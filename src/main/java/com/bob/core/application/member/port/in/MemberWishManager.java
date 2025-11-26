package com.bob.core.application.member.port.in;

import java.util.UUID;

import com.bob.core.application.member.dto.command.RegisterMemberWishCommand;
import com.bob.core.application.member.dto.command.RemoveMemberWishCommand;
import com.bob.core.domain.member.Member;

public interface MemberWishManager {

    Member registerWish(UUID memberId, RegisterMemberWishCommand command);

    Member removeWish(UUID memberId, RemoveMemberWishCommand command);
}
