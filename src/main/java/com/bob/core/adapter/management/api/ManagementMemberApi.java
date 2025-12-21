package com.bob.core.adapter.management.api;

import static com.bob.core.adapter.common.symbol.ResponseSymbol.UPDATED;

import java.util.UUID;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.core.adapter.common.CommonResponse;
import com.bob.core.adapter.common.symbol.ResponseSymbol;
import com.bob.core.adapter.management.api.request.ChangeManagementMemberStatusRequest;
import com.bob.core.adapter.management.api.request.ReadManagementMembersRequest;
import com.bob.core.application.management.dto.command.ChangeManagementMemberStatusCommand;
import com.bob.core.application.management.dto.result.ManagementMemberDetail;
import com.bob.core.application.management.port.in.ManagementMemberModifier;
import com.bob.core.application.management.port.in.ManagementMemberReader;
import com.bob.core.application.management.port.result.ManagementMemberSummaries;

@RestController
@RequestMapping("/management/members")
@RequiredArgsConstructor
public class ManagementMemberApi {

    private final ManagementMemberReader memberReader;
    private final ManagementMemberModifier memberModifier;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ManagementMemberSummaries readMembers(
        ReadManagementMembersRequest request,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return memberReader.readAll(request.key(), request.keyword(), pageable);
    }

    @GetMapping("/{memberId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ManagementMemberDetail readMemberDetail(@PathVariable UUID memberId) {
        return memberReader.readDetail(memberId);
    }

    @PatchMapping("/{memberId}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<ResponseSymbol> changeMemberStatus(
        @PathVariable UUID memberId,
        @Valid @RequestBody ChangeManagementMemberStatusRequest request
    ) {
        ChangeManagementMemberStatusCommand command
            = new ChangeManagementMemberStatusCommand(memberId, request.status(), request.memo());

        memberModifier.changeStatus(command);

        return new CommonResponse<>(true, UPDATED);
    }
}
