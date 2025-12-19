package com.bob.core.application.management.port.out;

import org.springframework.data.domain.Pageable;

import com.bob.core.application.management.port.result.ManagementMembersResult;

public interface ManagementMemberPort {

    ManagementMembersResult search(String key, String keyword, Pageable pageable);
}
