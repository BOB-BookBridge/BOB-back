package com.bob.admin.inquiry.application;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.admin.inquiry.application.dto.command.ProcessManagementInquiryCommand;
import com.bob.admin.inquiry.application.port.in.ManagementInquiryProcessor;
import com.bob.admin.inquiry.application.port.out.ManagementInquiryPort;

@Service
@Transactional
@RequiredArgsConstructor
public class ManagementInquiryCommandService implements ManagementInquiryProcessor {

    private final ManagementInquiryPort inquiryPort;

    @Override
    public void process(Long inquiryId, ProcessManagementInquiryCommand command) {
        inquiryPort.changeStatus(inquiryId, command.managerId(), command.status(), command.reply());
    }
}
