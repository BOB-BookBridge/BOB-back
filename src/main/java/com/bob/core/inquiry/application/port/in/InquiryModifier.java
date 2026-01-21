package com.bob.core.inquiry.application.port.in;

import com.bob.core.inquiry.application.dto.command.ChangeInquiryStatusCommand;
import com.bob.core.inquiry.domain.Inquiry;

public interface InquiryModifier {

    Inquiry changeStatus(Long inquiryId, ChangeInquiryStatusCommand command);
}
