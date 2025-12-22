package com.bob.core.member.application.port.in;

import java.util.UUID;

import com.bob.core.member.application.dto.command.RegisterMemberWishCommand;
import com.bob.core.member.application.dto.command.RemoveMemberWishCommand;
import com.bob.core.member.domain.Member;

public interface MemberWishManager {

    Member registerWish(UUID memberId, RegisterMemberWishCommand command);

    Member removeWish(UUID memberId, RemoveMemberWishCommand command);
}
