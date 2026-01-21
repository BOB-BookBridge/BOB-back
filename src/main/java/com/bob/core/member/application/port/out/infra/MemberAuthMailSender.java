package com.bob.core.member.application.port.out.infra;

public interface MemberAuthMailSender {

    void sendAuthCode(String email, String code);

    void sendTempPassword(String email, String password);
}
