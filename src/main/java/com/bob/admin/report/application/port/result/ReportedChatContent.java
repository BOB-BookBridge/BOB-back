package com.bob.admin.report.application.port.result;

import java.util.List;

public record ReportedChatContent(List<ReportedChatMessageInfo> messages) implements ReportedContent {

}
