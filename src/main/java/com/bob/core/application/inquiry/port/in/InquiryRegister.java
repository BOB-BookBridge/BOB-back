package com.bob.core.application.inquiry.port.in;

import com.bob.core.application.inquiry.dto.command.RegisterInquiryCommand;
import com.bob.core.domain.inquiry.Inquiry;

public interface InquiryRegister {

    Inquiry register(RegisterInquiryCommand command);
}
