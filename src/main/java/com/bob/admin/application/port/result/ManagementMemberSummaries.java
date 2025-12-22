package com.bob.admin.application.port.result;

import java.util.List;

public record ManagementMemberSummaries(Long totalCount, List<ManagementMember> members) {

}
