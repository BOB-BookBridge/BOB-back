package com.bob.infrastructure.mail.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import com.bob.infrastructure.mail.support.MailTemplateRenderer;

@DisplayName("구글 메일 전송 테스트")
@ExtendWith(MockitoExtension.class)
class GoogleMailSenderTest {

    @InjectMocks
    GoogleMailSender googleMailSender;

    @Mock
    JavaMailSender javaMailSender;

    @Mock
    MailTemplateRenderer mailTemplateRenderer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(googleMailSender, "address", "noreply@bob.com");
        ReflectionTestUtils.setField(googleMailSender, "personal", "Bookbridge");
    }

    @Test
    void 인증_코드_메일_전송() throws Exception {
        given(javaMailSender.createMimeMessage()).willReturn(new MimeMessage((Session)null));
        given(mailTemplateRenderer.render(eq("auth-code"), anyMap())).willReturn("<html>123456</html>");

        googleMailSender.sendAuthCode("test@example.com", "123456");

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        then(javaMailSender).should().send(captor.capture());

        MimeMessage message = captor.getValue();
        assertThat(message.getSubject()).isEqualTo("[BookBridge] 인증 코드 안내");

        InternetAddress from = (InternetAddress)message.getFrom()[0];
        assertThat(from.getAddress()).isEqualTo("noreply@bob.com");
        assertThat(from.getPersonal()).isEqualTo("Bookbridge");

        InternetAddress to = (InternetAddress)message.getRecipients(Message.RecipientType.TO)[0];
        assertThat(to.getAddress()).isEqualTo("test@example.com");
    }

    @Test
    void 임시_비밀번호_메일_전송() throws Exception {
        given(javaMailSender.createMimeMessage()).willReturn(new MimeMessage((Session)null));
        given(mailTemplateRenderer.render(eq("temp-password"), anyMap())).willReturn("<html>tempPass123</html>");

        googleMailSender.sendTempPassword("test@example.com", "tempPass123");

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        then(javaMailSender).should().send(captor.capture());

        MimeMessage message = captor.getValue();
        assertThat(message.getSubject()).isEqualTo("[BookBridge] 임시 비밀번호 안내");

        InternetAddress to = (InternetAddress)message.getRecipients(Message.RecipientType.TO)[0];
        assertThat(to.getAddress()).isEqualTo("test@example.com");
    }

    @Test
    void 문의_답변_메일_전송() throws Exception {
        given(javaMailSender.createMimeMessage()).willReturn(new MimeMessage((Session)null));
        given(mailTemplateRenderer.render(eq("inquiry-reply"), anyMap())).willReturn("<html>답변 내용</html>");

        googleMailSender.sendInquiryReply("test@example.com", "문의 내용", "답변 내용");

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        then(javaMailSender).should().send(captor.capture());

        MimeMessage message = captor.getValue();
        assertThat(message.getSubject()).isEqualTo("[BookBridge] 문의 답변 안내");

        InternetAddress to = (InternetAddress)message.getRecipients(Message.RecipientType.TO)[0];
        assertThat(to.getAddress()).isEqualTo("test@example.com");
    }

    @Test
    void 메일_전송_실패_로깅() {
        MimeMessage mime = new MimeMessage((Session)null);
        given(javaMailSender.createMimeMessage()).willReturn(mime);
        given(mailTemplateRenderer.render(eq("auth-code"), anyMap())).willReturn("<html>123456</html>");

        Logger logger = (Logger)LoggerFactory.getLogger(GoogleMailSender.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        willThrow(new RuntimeException("SMTP error")).given(javaMailSender).send(any(MimeMessage.class));

        googleMailSender.sendAuthCode("error@example.com", "123456");

        ILoggingEvent event = appender.list.get(0);
        assertThat(event.getThrowableProxy()).isNotNull();
        assertThat(event.getThrowableProxy().getMessage()).contains("SMTP error");

        logger.detachAppender(appender);
        appender.stop();
    }
}
