package com.bob.core.report.adapter.out;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.admin.post.application.port.out.ManagementPostReportPort;
import com.bob.core.report.application.dto.query.ReadReportsByTargetQuery;
import com.bob.core.report.application.port.in.ReportReader;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.ReportTarget;

@Component
@RequiredArgsConstructor
public class ManagementPostReportAdapter implements ManagementPostReportPort {

    private final ReportReader reportReader;

    @Override
    public List<String> readReasonsByPostId(Long postId) {
        var query = new ReadReportsByTargetQuery(ReportTarget.POST, postId);

        return reportReader.read(query).stream()
            .map(Report::getReason)
            .toList();
    }
}
