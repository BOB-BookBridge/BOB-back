package com.bob.core.management.application.port.result;

import java.util.List;

public record ManagementMemberSummaries(Long totalCount, List<ManagementMember> members) {

}
