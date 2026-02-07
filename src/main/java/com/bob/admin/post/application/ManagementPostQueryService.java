package com.bob.admin.post.application;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.admin.post.application.port.in.ManagementPostReader;
import com.bob.admin.post.application.port.out.ManagementPostMemberPort;
import com.bob.admin.post.application.port.out.ManagementPostPort;
import com.bob.admin.post.application.port.out.ManagementPostReportPort;
import com.bob.admin.post.application.port.result.ManagementPost;
import com.bob.admin.post.application.port.result.ManagementPostDetail;
import com.bob.admin.post.application.port.result.ManagementPostReports;
import com.bob.admin.post.application.port.result.ManagementPostSummaries;
import com.bob.admin.post.domain.PostManagementHistory;
import com.bob.admin.post.domain.repository.PostManagementHistoryRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ManagementPostQueryService implements ManagementPostReader {

    private final PostManagementHistoryRepository historyRepository;

    private final ManagementPostPort postPort;
    private final ManagementPostReportPort reportPort;
    private final ManagementPostMemberPort memberPort;

    @Override
    public ManagementPostSummaries readAll(String email, String status, Pageable pageable) {
        return postPort.readAll(email, status, pageable);
    }

    @Override
    public ManagementPostDetail readDetail(Long postId) {
        ManagementPost post = postPort.read(postId);

        List<String> reasons = reportPort.readReasonsByPostId(postId);
        ManagementPostReports reports = new ManagementPostReports(reasons.size(), reasons);

        PostManagementHistory history = historyRepository.findTopByPostIdOrderByProcessedAtDesc(postId).orElse(null);

        String managerNickname = history != null
            ? memberPort.readNickname(history.getManagerId())
            : null;

        return ManagementPostDetail.of(post, reports, history, managerNickname);
    }
}
