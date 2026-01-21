package com.bob.core.inquiry.application;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.inquiry.application.dto.command.ChangeInquiryStatusCommand;
import com.bob.core.inquiry.application.dto.command.RegisterInquiryCommand;
import com.bob.core.inquiry.application.port.in.InquiryModifier;
import com.bob.core.inquiry.application.port.in.InquiryReader;
import com.bob.core.inquiry.application.port.in.InquiryRegister;
import com.bob.core.inquiry.application.port.out.InquiryMemberPort;
import com.bob.core.inquiry.application.port.out.infra.InquiryMailSender;
import com.bob.core.inquiry.domain.Inquiry;
import com.bob.core.inquiry.domain.repository.InquiryRepository;
import com.bob.core.inquiry.event.InquiryProcessedEvent;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryCommandService implements InquiryRegister, InquiryModifier {

    private final InquiryRepository inquiryRepository;
    private final InquiryReader inquiryReader;

    private final InquiryMemberPort memberPort;

    private final InquiryMailSender mailSender;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Inquiry register(RegisterInquiryCommand command) {
        Inquiry inquiry = Inquiry.createInquiry(command.email(), command.title(), command.content());

        return inquiryRepository.save(inquiry);
    }

    @Override
    public Inquiry changeStatus(Long inquiryId, ChangeInquiryStatusCommand command) {
        Inquiry inquiry = inquiryReader.read(inquiryId);

        switch (command.status()) {
            case IN_REVIEW -> inquiry.review(command.managerId());
            case PROCESSED -> {
                inquiry.process(command.reply());
                sendNotificationOrEmail(inquiry);
            }
            default -> inquiry.close();
        }

        return inquiry;
    }

    private void sendNotificationOrEmail(Inquiry inquiry) {
        memberPort.findByEmail(inquiry.getEmail()).ifPresentOrElse(
            member -> eventPublisher.publishEvent(new InquiryProcessedEvent(inquiry.getId(), member.id())),
            () -> mailSender.sendInquiryReply(inquiry.getEmail(), inquiry.getContent(), inquiry.getReply())
        );
    }
}
