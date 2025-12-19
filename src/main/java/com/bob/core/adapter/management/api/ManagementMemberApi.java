package com.bob.core.adapter.management.api;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.core.adapter.management.api.request.ReadManagementMembersRequest;
import com.bob.core.application.management.port.in.ManagementMemberReader;
import com.bob.core.application.management.port.result.ManagementMembersResult;

@RestController
@RequestMapping("/management/members")
@RequiredArgsConstructor
public class ManagementMemberApi {

    private final ManagementMemberReader memberReader;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ManagementMembersResult readMembers(
        ReadManagementMembersRequest request,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return memberReader.readMembers(request.key(), request.keyword(), pageable);
    }
}
