package com.bob.admin.post.application.port.in;

import org.springframework.data.domain.Pageable;

import com.bob.admin.post.application.port.result.ManagementPostSummaries;

public interface ManagementPostReader {

    ManagementPostSummaries readAll(String email, String status, Pageable pageable);
}
