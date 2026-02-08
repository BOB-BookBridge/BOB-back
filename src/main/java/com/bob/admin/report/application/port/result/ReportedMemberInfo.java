package com.bob.admin.report.application.port.result;

import java.util.UUID;

public record ReportedMemberInfo(UUID id, int count, String currentTarget, Long currentTargetId) {

}
