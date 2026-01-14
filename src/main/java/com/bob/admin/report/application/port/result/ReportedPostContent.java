package com.bob.admin.report.application.port.result;

public record ReportedPostContent(Long id, String title, String description, String thumbnailUrl
) implements ReportedContent {

}
