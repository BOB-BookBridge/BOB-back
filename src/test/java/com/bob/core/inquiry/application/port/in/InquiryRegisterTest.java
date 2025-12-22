package com.bob.core.inquiry.application.port.in;

import static com.bob.core.inquiry.domain.InquiryStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.inquiry.application.dto.command.RegisterInquiryCommand;
import com.bob.core.inquiry.domain.Inquiry;
import com.bob.support.annotation.ContainerTest;

@DisplayName("문의 등록 테스트")
@ContainerTest
record InquiryRegisterTest(InquiryRegister inquiryRegister) {

    @Test
    void 문의_등록() {
        RegisterInquiryCommand command = new RegisterInquiryCommand("test@email.com", "제목", "내용");

        Inquiry inquiry = inquiryRegister.register(command);

        assertThat(inquiry.getId()).isNotNull();
        assertThat(inquiry.getStatus()).isEqualTo(PENDING);
    }
}
