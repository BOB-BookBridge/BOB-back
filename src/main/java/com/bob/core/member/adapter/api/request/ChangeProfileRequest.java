package com.bob.core.member.adapter.api.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangeProfileRequest(
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(max = 12, message = "닉네임은 12자 이하로 입력해 주세요.")
    String nickname,

    @NotNull(message = "지역 ID는 필수입니다.")
    Integer emdId,

    @NotNull(message = "지역 인증 여부는 필수입니다.")
    Boolean areaAuthenticate,
    Double lat,
    Double lon,

    @NotNull(message = "관심사 목록은 필수입니다.")
    @Size(max = 20, message = "관심사는 최대 20개까지 등록 가능합니다.")
    List<@Pattern(regexp = "^(?!\\s)(.*\\S)?$", message = "관심사는 앞뒤에 공백이 올 수 없습니다.") String> interests
) {

}
