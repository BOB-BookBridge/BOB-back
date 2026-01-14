package com.bob.admin.report.application.port.result;

import java.time.LocalDateTime;

public record ReportedChatMessageInfo(String content, LocalDateTime sentAt) {

}
