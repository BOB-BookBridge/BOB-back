package com.bob.admin.post.application.port.out;

import org.springframework.data.domain.Pageable;

import com.bob.admin.post.application.port.result.ManagementPost;
import com.bob.admin.post.application.port.result.ManagementPostSummaries;

public interface ManagementPostPort {

    ManagementPostSummaries readAll(String email, String status, Pageable pageable);

    ManagementPost read(Long postId);

    String changeStatus(Long postId, String status);
}
