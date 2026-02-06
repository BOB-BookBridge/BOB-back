package com.bob.admin.post.adapter.api.request;

import com.bob.shared.web.annotation.AllowedValues;

public record ReadManagementPostsRequest(
    String email,

    @AllowedValues(
        value = {"PENDING", "BANNED"},
        ignoreCase = true
    )
    String status
) {

}
