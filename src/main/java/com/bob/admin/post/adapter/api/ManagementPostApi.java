package com.bob.admin.post.adapter.api;

import static com.bob.shared.web.response.ResponseSymbol.UPDATED;

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

import com.bob.admin.post.adapter.api.request.ProcessManagementPostStatusRequest;
import com.bob.admin.post.adapter.api.request.ReadManagementPostsRequest;
import com.bob.admin.post.application.dto.command.ProcessManagementPostStatusCommand;
import com.bob.admin.post.application.port.in.ManagementPostProcessor;
import com.bob.admin.post.application.port.in.ManagementPostReader;
import com.bob.admin.post.application.port.result.ManagementPostDetail;
import com.bob.admin.post.application.port.result.ManagementPostSummaries;
import com.bob.shared.web.annotation.AuthenticationId;
import com.bob.shared.web.response.CommonResponse;
import com.bob.shared.web.response.ResponseSymbol;

@RestController
@RequestMapping("/management/posts")
@RequiredArgsConstructor
public class ManagementPostApi {

    private final ManagementPostReader postReader;
    private final ManagementPostProcessor postProcessor;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ManagementPostSummaries readPosts(
        @Valid ReadManagementPostsRequest request,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return postReader.readAll(request.email(), request.status(), pageable);
    }

    @GetMapping("/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ManagementPostDetail readPostDetail(@PathVariable Long postId) {
        return postReader.readDetail(postId);
    }

    @PatchMapping("/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<ResponseSymbol> processPost(
        @AuthenticationId UUID managerId,
        @PathVariable Long postId,
        @Valid @RequestBody ProcessManagementPostStatusRequest request
    ) {
        var command = new ProcessManagementPostStatusCommand(managerId, request.status(), request.memo());

        postProcessor.process(postId, command);

        return new CommonResponse<>(true, UPDATED);
    }
}
