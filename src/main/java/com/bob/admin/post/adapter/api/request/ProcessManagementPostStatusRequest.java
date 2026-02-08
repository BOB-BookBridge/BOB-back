package com.bob.admin.post.adapter.api.request;

import jakarta.validation.constraints.Size;

import com.bob.shared.web.annotation.AllowedValues;

public record ProcessManagementPostStatusRequest(
    @AllowedValues(
        value = {"ACTIVE", "DEACTIVATED", "BANNED"},
        ignoreCase = true,
        allowNull = false
    )
    String status,

    @Size(max = 200, message = "관리자 메모는 200자 이하로 입력해 주세요")
    String memo
) {

}
