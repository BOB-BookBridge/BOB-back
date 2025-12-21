package com.bob.core.application.management.port.out;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.bob.core.application.management.port.result.ManagementMember;
import com.bob.core.application.management.port.result.ManagementMemberSummaries;

public interface ManagementMemberPort {

    ManagementMember read(UUID memberId);

    ManagementMemberSummaries search(String key, String keyword, Pageable pageable);

    ManagementMember changeStatus(UUID memberId, String status, String memo);
}
