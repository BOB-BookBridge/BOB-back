package com.bob.core.adapter.inquiry.api;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bob.core.adapter.inquiry.api.request.RegisterInquiryRequest;
import com.bob.core.adapter.inquiry.api.response.RegisterInquiryResponse;
import com.bob.core.application.inquiry.dto.command.RegisterInquiryCommand;
import com.bob.core.application.inquiry.port.in.InquiryRegister;
import com.bob.core.domain.inquiry.Inquiry;

@RestController
@RequestMapping("/inquiries")
@RequiredArgsConstructor
public class InquiryApi {

    private final InquiryRegister inquiryRegister;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterInquiryResponse registerInquiry(@Valid @RequestBody RegisterInquiryRequest request) {
        RegisterInquiryCommand command = new RegisterInquiryCommand(request.email(), request.title(),
            request.content());

        Inquiry inquiry = inquiryRegister.register(command);

        return RegisterInquiryResponse.of(inquiry);
    }
}
