package com.bob.core.adapter.member.api.request;

import jakarta.validation.constraints.NotBlank;

public record ChangeMemberProfileImageRequest(
    @NotBlank(message = "fileName은 필수입니다.")
    String fileName
) {

}
