package com.bob.admin.member.application.dto.result;

import com.bob.admin.member.application.port.result.ManagementMember;
import com.bob.admin.member.application.port.result.ManagementMemberActivity;
import com.bob.admin.member.application.port.result.ManagementMemberReport;

public record ManagementMemberDetail(
    ManagementMember member,
    ManagementMemberActivity activities,
    ManagementMemberReport reports
) {

}
