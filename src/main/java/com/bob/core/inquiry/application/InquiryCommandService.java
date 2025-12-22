package com.bob.core.inquiry.application;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.inquiry.application.dto.command.RegisterInquiryCommand;
import com.bob.core.inquiry.application.port.in.InquiryRegister;
import com.bob.core.inquiry.domain.Inquiry;
import com.bob.core.inquiry.domain.repository.InquiryRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryCommandService implements InquiryRegister {

    private final InquiryRepository inquiryRepository;

    @Override
    public Inquiry register(RegisterInquiryCommand command) {
        Inquiry inquiry = Inquiry.createInquiry(command.email(), command.title(), command.content());

        return inquiryRepository.save(inquiry);
    }
}
