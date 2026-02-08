package com.bob.admin.post.application;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.bob.admin.post.application.dto.command.ProcessManagementPostStatusCommand;
import com.bob.admin.post.application.port.in.ManagementPostProcessor;
import com.bob.admin.post.application.port.out.ManagementPostMemberPort;
import com.bob.admin.post.application.port.out.ManagementPostPort;
import com.bob.admin.post.application.port.out.ManagementPostReportPort;
import com.bob.admin.post.application.port.result.ManagementPost;
import com.bob.admin.post.domain.ManagementStatus;
import com.bob.admin.post.domain.PostManagementHistory;
import com.bob.admin.post.domain.repository.PostManagementHistoryRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ManagementPostCommandService implements ManagementPostProcessor {

    private final PostManagementHistoryRepository historyRepository;

    private final ManagementPostPort postPort;
    private final ManagementPostReportPort reportPort;
    private final ManagementPostMemberPort memberPort;

    @Override
    public void process(Long postId, ProcessManagementPostStatusCommand command) {
        String previousStatus = postPort.changeStatus(postId, command.status());

        PostManagementHistory history = PostManagementHistory.create(
            postId,
            command.managerId(),
            ManagementStatus.valueOf(previousStatus),
            ManagementStatus.valueOf(command.status()),
            command.memo()
        );

        if ("BANNED".equals(command.status())) {
            ManagementPost post = postPort.read(postId);

            Integer reportedCount = reportPort.register(command.managerId(), post.writer().id(), postId);
            if (reportedCount >= 3)
                memberPort.ban(post.writer().id());
        }

        historyRepository.save(history);
    }
}
