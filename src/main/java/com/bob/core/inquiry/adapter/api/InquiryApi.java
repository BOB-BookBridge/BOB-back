package com.bob.core.inquiry.adapter.api;

import java.util.UUID;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bob.core.inquiry.adapter.api.request.RegisterInquiryRequest;
import com.bob.core.inquiry.adapter.api.response.RegisterInquiryResponse;
import com.bob.core.inquiry.application.dto.command.RegisterInquiryCommand;
import com.bob.core.inquiry.application.dto.query.ReadInquiryDetailQuery;
import com.bob.core.inquiry.application.dto.result.InquiryDetail;
import com.bob.core.inquiry.application.port.in.InquiryReader;
import com.bob.core.inquiry.application.port.in.InquiryRegister;
import com.bob.core.inquiry.domain.Inquiry;
import com.bob.shared.web.annotation.AuthenticationId;

@RestController
@RequestMapping("/inquiries")
@RequiredArgsConstructor
public class InquiryApi {

    private final InquiryRegister inquiryRegister;
    private final InquiryReader inquiryReader;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterInquiryResponse registerInquiry(@Valid @RequestBody RegisterInquiryRequest request) {
        RegisterInquiryCommand command = new RegisterInquiryCommand(request.email(), request.title(),
            request.content());

        Inquiry inquiry = inquiryRegister.register(command);

        return RegisterInquiryResponse.of(inquiry);
    }

    @GetMapping("/{inquiryId}")
    public InquiryDetail readDetail(@PathVariable Long inquiryId, @AuthenticationId UUID memberId) {
        ReadInquiryDetailQuery query = new ReadInquiryDetailQuery(memberId);

        return inquiryReader.readDetail(inquiryId, query);
    }
}
