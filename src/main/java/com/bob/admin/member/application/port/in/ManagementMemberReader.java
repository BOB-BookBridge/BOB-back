package com.bob.admin.member.application.port.in;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.bob.admin.member.application.dto.result.ManagementMemberDetail;
import com.bob.admin.member.application.port.result.ManagementMemberSummaries;

public interface ManagementMemberReader {

    ManagementMemberSummaries readAll(String key, String keyword, Pageable pageable);

    ManagementMemberDetail readDetail(UUID memberId);
}
