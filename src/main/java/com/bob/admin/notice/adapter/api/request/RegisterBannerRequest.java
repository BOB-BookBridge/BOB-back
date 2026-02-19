package com.bob.admin.notice.adapter.api.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterBannerRequest(
    @NotBlank(message = "공지 내용은 필수입니다")
    @Size(max = 200, message = "내용은 1자 이상 200자 이하로 입력해 주세요")
    String content,

    LocalDateTime endTime
) {

}
