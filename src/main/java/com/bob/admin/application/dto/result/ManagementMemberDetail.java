package com.bob.admin.application.dto.result;

import com.bob.admin.application.port.result.ManagementMember;
import com.bob.admin.application.port.result.ManagementMemberActivity;
import com.bob.admin.application.port.result.ManagementMemberReport;

public record ManagementMemberDetail(
    ManagementMember member,
    ManagementMemberActivity activities,
    ManagementMemberReport reports
) {

}
