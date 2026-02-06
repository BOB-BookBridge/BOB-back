package com.bob.admin.post.application;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.admin.post.application.port.in.ManagementPostReader;
import com.bob.admin.post.application.port.out.ManagementPostPort;
import com.bob.admin.post.application.port.result.ManagementPostSummaries;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ManagementPostQueryService implements ManagementPostReader {

    private final ManagementPostPort postPort;

    @Override
    public ManagementPostSummaries readAll(String email, String status, Pageable pageable) {
        return postPort.readAll(email, status, pageable);
    }
}
