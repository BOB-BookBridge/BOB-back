package com.bob.core.application.member.port.out;

import java.util.List;
import java.util.UUID;

import com.bob.core.application.member.port.result.MemberBookcaseResult;

public interface MemberBookcasePort {

    List<MemberBookcaseResult> readAllDetail(UUID memberId);
}
