package com.bob.core.application.management.dto.result;

import com.bob.core.application.management.port.result.ManagementMember;
import com.bob.core.application.management.port.result.ManagementMemberActivity;
import com.bob.core.application.management.port.result.ManagementMemberReport;

public record ManagementMemberDetail(
    ManagementMember member,
    ManagementMemberActivity activities,
    ManagementMemberReport reports
) {

}
