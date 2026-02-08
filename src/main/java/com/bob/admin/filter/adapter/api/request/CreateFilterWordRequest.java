package com.bob.admin.filter.adapter.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateFilterWordRequest(

    @NotNull
    @Size(min = 1, max = 100, message = "금칙어는 1자 이상, 20자 이하로 입력해주세요")
    String word
) {

}
