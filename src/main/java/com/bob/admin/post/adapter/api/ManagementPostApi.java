package com.bob.admin.post.adapter.api;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.admin.post.adapter.api.request.ReadManagementPostsRequest;
import com.bob.admin.post.application.port.in.ManagementPostReader;
import com.bob.admin.post.application.port.result.ManagementPostDetail;
import com.bob.admin.post.application.port.result.ManagementPostSummaries;

@RestController
@RequestMapping("/management/posts")
@RequiredArgsConstructor
public class ManagementPostApi {

    private final ManagementPostReader postReader;

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
}
