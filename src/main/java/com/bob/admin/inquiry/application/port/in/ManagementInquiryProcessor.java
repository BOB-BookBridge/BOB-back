package com.bob.admin.inquiry.application.port.in;

import com.bob.admin.inquiry.application.dto.command.ProcessManagementInquiryCommand;

public interface ManagementInquiryProcessor {

    void process(Long inquiryId, ProcessManagementInquiryCommand command);
}
