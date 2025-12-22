package com.bob.core.report.adapter.api;

import static com.bob.core.report.domain.ReportTarget.CHAT;
import static com.bob.core.report.domain.ReportTarget.POST;

import java.util.UUID;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bob.core.report.adapter.api.request.RegisterReportRequest;
import com.bob.core.report.adapter.api.response.RegisterReportResponse;
import com.bob.core.report.application.dto.command.RegisterReportCommand;
import com.bob.core.report.application.port.in.ReportRegister;
import com.bob.core.report.domain.Report;
import com.bob.core.shared.web.AuthenticationId;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportApi {

    private final ReportRegister reportRegister;

    @PostMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterReportResponse registerPostReport(
        @PathVariable Long postId,
        @AuthenticationId UUID reporterId,
        @Valid @RequestBody RegisterReportRequest request
    ) {
        RegisterReportCommand command
            = new RegisterReportCommand(POST, postId, request.reason(), reporterId, request.reportedId());

        Report report = reportRegister.register(command);

        return RegisterReportResponse.of(report);
    }

    @PostMapping("/chats/{messageId}")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterReportResponse registerChatReport(
        @PathVariable Long messageId,
        @AuthenticationId UUID reporterId,
        @Valid @RequestBody RegisterReportRequest request
    ) {
        RegisterReportCommand command
            = new RegisterReportCommand(CHAT, messageId, request.reason(), reporterId, request.reportedId());

        Report report = reportRegister.register(command);

        return RegisterReportResponse.of(report);
    }
}
