package com.bob.admin.member.adapter.api.request;

import com.bob.shared.web.annotation.AllowedValues;

public record ReadManagementMembersRequest(
    @AllowedValues(value = {"EMAIL", "NICKNAME"}, ignoreCase = true)
    String key,

    String keyword
) {

}
