package com.bob.core.application.management.port.result;

import java.util.List;

public record ManagementMemberSummaries(Long totalCount, List<ManagementMember> members) {

}
