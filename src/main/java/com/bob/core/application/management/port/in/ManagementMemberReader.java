package com.bob.core.application.management.port.in;

import org.springframework.data.domain.Pageable;

import com.bob.core.application.management.port.result.ManagementMembersResult;

public interface ManagementMemberReader {

    ManagementMembersResult readMembers(String key, String keyword, Pageable pageable);
}
