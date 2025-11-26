package com.bob.infrastructure.mail.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

@DisplayName("구글 메일 전송 테스트")
@ExtendWith(MockitoExtension.class)
class GoogleMailSenderTest {

    @InjectMocks
    GoogleMailSender googleMailSender;

    @Mock
    JavaMailSender javaMailSender;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(googleMailSender, "address", "noreply@bob.com");
        ReflectionTestUtils.setField(googleMailSender, "personal", "Bookbridge");
    }

    @Test
    void 메일_전송() throws Exception {
        given(javaMailSender.createMimeMessage()).willReturn(new MimeMessage((Session)null));
        googleMailSender.send("test@example.com", "test subject", "test body");

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        then(javaMailSender).should().send(captor.capture());

        MimeMessage message = captor.getValue();
        assertThat(message.getSubject()).isEqualTo("[BookBridge] " + "test subject" + " 안내");

        InternetAddress from = (InternetAddress)message.getFrom()[0];
        assertThat(from.getAddress()).isEqualTo("noreply@bob.com");
        assertThat(from.getPersonal()).isEqualTo("Bookbridge");

        InternetAddress to = (InternetAddress)message.getRecipients(Message.RecipientType.TO)[0];
        assertThat(to.getAddress()).isEqualTo("test@example.com");
    }

    @Test
    void 메일_전송_실패_로깅() {
        MimeMessage mime = new MimeMessage((Session)null);
        given(javaMailSender.createMimeMessage()).willReturn(mime);

        Logger logger = (Logger)LoggerFactory.getLogger(GoogleMailSender.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        willThrow(new RuntimeException("SMTP error")).given(javaMailSender).send(any(MimeMessage.class));

        googleMailSender.send("error@example.com", "subject", "body");

        ILoggingEvent event = appender.list.get(0);
        assertThat(event.getThrowableProxy()).isNotNull();
        assertThat(event.getThrowableProxy().getMessage()).contains("SMTP error");

        logger.detachAppender(appender);
        appender.stop();
    }
}
