package com.bob.core.inquiry.application.port.in;

import com.bob.core.inquiry.application.dto.command.RegisterInquiryCommand;
import com.bob.core.inquiry.domain.Inquiry;

public interface InquiryRegister {

    Inquiry register(RegisterInquiryCommand command);
}
