package com.bob.core.member.adapter.api.request;

import jakarta.validation.constraints.NotBlank;

public record ChangeMemberProfileImageRequest(
    @NotBlank(message = "fileName은 필수입니다.")
    String fileName
) {

}
