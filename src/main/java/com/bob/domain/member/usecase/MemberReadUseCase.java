package com.bob.domain.member.usecase;

import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;

public interface MemberReadUseCase {

  MemberProfileResponse readProfileProcess(ReadProfileQuery query);
}
