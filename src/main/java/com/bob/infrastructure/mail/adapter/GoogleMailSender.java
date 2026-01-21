package com.bob.infrastructure.mail.adapter;

import java.util.Map;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bob.core.inquiry.application.port.out.infra.InquiryMailSender;
import com.bob.core.member.application.port.out.infra.MemberAuthMailSender;
import com.bob.infrastructure.mail.support.MailTemplateRenderer;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleMailSender implements MemberAuthMailSender, InquiryMailSender {

    private final JavaMailSender mailSender;

    private final MailTemplateRenderer mailTemplateRenderer;

    @Value("${mail.from.address}")
    private String address;

    @Value("${mail.from.personal}")
    private String personal;

    @Async
    @Override
    public void sendAuthCode(String email, String code) {
        String htmlBody = mailTemplateRenderer.render("auth-code", Map.of("code", code));
        sendMail(email, "인증 코드", htmlBody);
    }

    @Async
    @Override
    public void sendTempPassword(String email, String password) {
        String htmlBody = mailTemplateRenderer.render("temp-password", Map.of("password", password));
        sendMail(email, "임시 비밀번호", htmlBody);
    }

    @Async
    @Override
    public void sendInquiryReply(String email, String content, String reply) {
        String htmlBody = mailTemplateRenderer.render("inquiry-reply", Map.of("content", content, "reply", reply));
        sendMail(email, "문의 답변", htmlBody);
    }

    private void sendMail(String email, String subject, String htmlBody) {
        MimeMessage mime = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");

            helper.setFrom(new InternetAddress(address, personal, "UTF-8"));
            helper.setTo(email);
            helper.setSubject("[BookBridge] " + subject + " 안내");
            helper.setText(htmlBody, true);
            helper.addInline("logo", new ClassPathResource("templates/logo.png"));

            mailSender.send(mime);
        } catch (Exception e) {
            log.warn("Failed to send email : to={}, subject={}", email, subject, e);
        }
    }
}
