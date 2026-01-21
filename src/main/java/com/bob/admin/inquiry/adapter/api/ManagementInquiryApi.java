package com.bob.admin.inquiry.adapter.api;

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

import com.bob.admin.inquiry.adapter.api.request.ProcessManagementInquiryRequest;
import com.bob.admin.inquiry.adapter.api.request.ReadManagementInquiriesRequest;
import com.bob.admin.inquiry.application.dto.command.ProcessManagementInquiryCommand;
import com.bob.admin.inquiry.application.dto.query.ReadManagementInquiriesQuery;
import com.bob.admin.inquiry.application.port.in.ManagementInquiryProcessor;
import com.bob.admin.inquiry.application.port.in.ManagementInquiryReader;
import com.bob.admin.inquiry.application.port.result.ManagementInquirySummaries;
import com.bob.shared.web.annotation.AuthenticationId;
import com.bob.shared.web.response.CommonResponse;
import com.bob.shared.web.response.ResponseSymbol;

@RestController
@RequestMapping("/management/inquiries")
@RequiredArgsConstructor
public class ManagementInquiryApi {

    private final ManagementInquiryProcessor inquiryProcessor;
    private final ManagementInquiryReader inquiryReader;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ManagementInquirySummaries readInquiries(
        @Valid ReadManagementInquiriesRequest request,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        ReadManagementInquiriesQuery query = new ReadManagementInquiriesQuery(request.email(), request.status());

        return inquiryReader.readAll(query, pageable);
    }

    @PatchMapping("/{inquiryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<ResponseSymbol> processInquiry(
        @AuthenticationId UUID managerId,
        @PathVariable Long inquiryId,
        @Valid @RequestBody ProcessManagementInquiryRequest request
    ) {
        ProcessManagementInquiryCommand command = new ProcessManagementInquiryCommand(
            managerId,
            request.status(),
            request.reply()
        );

        inquiryProcessor.process(inquiryId, command);

        return new CommonResponse<>(true, UPDATED);
    }
}
