package com.bob.core.adapter.member.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(max = 12, message = "닉네임은 12자 이하로 입력해 주세요.")
    String nickname,

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하로 입력해 주세요.")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]+$",
        message = "비밀번호는 영어 대소문자, 숫자를 포함해야 합니다."
    )
    String password,

    @NotNull(message = "행정동 ID는 필수입니다.")
    Integer emdId
) {

}
