package com.bob.core.management.application.dto.result;

import com.bob.core.management.application.port.result.ManagementMember;
import com.bob.core.management.application.port.result.ManagementMemberActivity;
import com.bob.core.management.application.port.result.ManagementMemberReport;

public record ManagementMemberDetail(
    ManagementMember member,
    ManagementMemberActivity activities,
    ManagementMemberReport reports
) {

}
