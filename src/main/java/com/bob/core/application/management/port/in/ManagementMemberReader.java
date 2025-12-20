package com.bob.core.application.management.port.in;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.bob.core.application.management.dto.result.ManagementMemberDetail;
import com.bob.core.application.management.port.result.ManagementMemberSummaries;

public interface ManagementMemberReader {

    ManagementMemberSummaries readAll(String key, String keyword, Pageable pageable);

    ManagementMemberDetail readDetail(UUID memberId);
}
