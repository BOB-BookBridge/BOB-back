package com.bob.core.application.inquiry;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.inquiry.dto.command.RegisterInquiryCommand;
import com.bob.core.application.inquiry.port.in.InquiryRegister;
import com.bob.core.domain.inquiry.Inquiry;
import com.bob.core.domain.inquiry.repository.InquiryRepository;

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
