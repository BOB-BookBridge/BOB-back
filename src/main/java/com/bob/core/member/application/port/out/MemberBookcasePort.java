package com.bob.core.member.application.port.out;

import java.util.List;
import java.util.UUID;

import com.bob.core.member.application.port.result.MemberBookcaseResult;

public interface MemberBookcasePort {

    List<MemberBookcaseResult> readAllDetail(UUID memberId);
}
