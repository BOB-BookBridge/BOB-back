package com.bob.infrastructure.mail.adapter;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bob.core.application.member.port.out.MailSender;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleMailSender implements MailSender {

    private final JavaMailSender mailSender;

    @Value("${mail.from.address}")
    private String address;

    @Value("${mail.from.personal}")
    private String personal;

    @Async
    public void send(String email, String subject, String body) {
        MimeMessage mime = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");

            helper.setFrom(new InternetAddress(address, personal, "UTF-8"));
            helper.setTo(email);
            helper.setSubject("[BookBridge] " + subject + " 안내");
            helper.setText(subject + " : " + body, false);

            mailSender.send(mime);
        } catch (Exception e) {
            log.warn("Failed to send email : to={}, subject={}", email, subject, e);
        }
    }
}