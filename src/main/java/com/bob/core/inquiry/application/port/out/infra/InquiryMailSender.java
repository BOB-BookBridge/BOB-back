package com.bob.core.inquiry.application.port.out.infra;

public interface InquiryMailSender {

    void sendInquiryReply(String email, String content, String reply);
}
