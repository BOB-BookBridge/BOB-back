package com.bob.core.member.adapter.api.request;

import jakarta.validation.constraints.NotBlank;

public record IssuePasswordRequest(
    @NotBlank(message = "이메일은 필수입니다.")
    String email
) {

}
