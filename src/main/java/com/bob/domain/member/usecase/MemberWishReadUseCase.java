package com.bob.domain.member.usecase;

import com.bob.domain.member.service.dto.query.ReadMemberWishesQuery;
import com.bob.domain.member.service.dto.response.MemberWishesResult;

public interface MemberWishReadUseCase {

  MemberWishesResult readWishesProcess(ReadMemberWishesQuery query);
}
