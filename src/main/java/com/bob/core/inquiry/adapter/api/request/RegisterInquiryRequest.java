package com.bob.core.inquiry.adapter.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterInquiryRequest(
    @NotNull(message = "문의자 이메일은 필수입니다")
    String email,

    @NotNull(message = "문의 제목은 필수입니다")
    @Size(max = 50, message = "제목은 50자 이하로 입력해 주세요.")
    String title,

    @NotNull(message = "문의 내용은 필수입니다")
    @Size(max = 200, message = "내용은 200자 이하로 입력해 주세요.")
    String content
) {

}
