package com.bob.admin.report.application.port.result;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ReportedPostContent.class),
    @JsonSubTypes.Type(value = ReportedChatContent.class)
})
public sealed interface ReportedContent permits ReportedPostContent, ReportedChatContent {

}
