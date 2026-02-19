package com.bob.admin.notice.adapter.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.bob.shared.web.annotation.NotBlankIfNotNull;

public record RegisterAlertRequest(
    @NotBlankIfNotNull(message = "제목은 공백일 수 없습니다")
    @Size(max = 100, message = "제목은 100자 이하로 입력해 주세요")
    String title,

    @NotBlank(message = "내용은 필수입니다")
    @Size(max = 500, message = "내용은 500자 이하로 입력해 주세요")
    String content
) {

}
