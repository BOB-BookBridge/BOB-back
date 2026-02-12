package com.bob.core.report.event;

import java.util.UUID;

public record ReportNotificationEvent(Long reportId, UUID reportedId) {

}
