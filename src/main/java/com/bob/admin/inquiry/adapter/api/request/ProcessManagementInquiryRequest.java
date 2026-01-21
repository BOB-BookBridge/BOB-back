package com.bob.admin.inquiry.adapter.api.request;

import jakarta.validation.constraints.Size;

import com.bob.shared.web.annotation.AllowedValues;

public record ProcessManagementInquiryRequest(
    @AllowedValues(
        value = {"IN_REVIEW", "PROCESSED", "CLOSED"},
        ignoreCase = true,
        allowNull = false
    )
    String status,

    @Size(max = 200, message = "관리자 메모는 200자 이하로 입력해 주세요")
    String reply
) {

}
