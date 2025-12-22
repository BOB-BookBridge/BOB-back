package com.bob.admin.member.adapter.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangeManagementMemberStatusRequest(
    @NotNull(message = "변경하고자 하는 상태는 필수입니다")
    String status,

    @Size(max = 200, message = "관리자 메모는 200자 이하로 입력해 주세요")
    String memo
) {

}
