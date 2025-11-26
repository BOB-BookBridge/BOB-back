package com.bob.core.application.member.port.out;

public interface MailSender {

    void send(String email, String subject, String body);
}
