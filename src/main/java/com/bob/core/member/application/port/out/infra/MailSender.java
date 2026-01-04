package com.bob.core.member.application.port.out.infra;

public interface MailSender {

    void send(String email, String subject, String body);
}
