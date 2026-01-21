package com.bob.admin.inquiry.adapter.api.request;

import com.bob.shared.web.annotation.AllowedValues;

public record ReadManagementInquiriesRequest(
    String email,

    @AllowedValues(
        value = {"PENDING", "IN_REVIEW", "PROCESSED", "CLOSED"},
        ignoreCase = true
    )
    String status
) {

}
