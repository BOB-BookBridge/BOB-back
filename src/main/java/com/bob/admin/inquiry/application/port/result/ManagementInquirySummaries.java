package com.bob.admin.inquiry.application.port.result;

import java.util.List;

public record ManagementInquirySummaries(Long totalCount, List<ManagementInquiry> inquiries) {

}
