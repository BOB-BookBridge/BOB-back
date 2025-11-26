package com.bob.core.adapter.notification.api;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.notification.domain.NotificationFixture.createNotification;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.core.adapter.notification.api.response.NotificationResponse;
import com.bob.core.domain.notification.Notification;
import com.bob.core.domain.notification.repository.NotificationRepository;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;

@DisplayName("알림 API 테스트")
@BobApiTest
record NotificationApiTest(
    MockMvcTester mvcTester, NotificationRepository notificationRepository, EntityManager em, ObjectMapper objectMapper
) {

    @Test
    void 알림_목록_조회() throws Exception {
        notificationRepository.save(createNotification("1", MEMBER_ID, "알림1", false));
        notificationRepository.save(createNotification("2", MEMBER_ID, "알림2", false));
        notificationRepository.save(createNotification("3", OTHER_MEMBER_ID, "타인 알림", false));

        setAuthentication();

        MvcTestResult result = mvcTester.get()
            .uri("/notifications")
            .exchange();

        assertThat(result).hasStatusOk();

        List<NotificationResponse> response = objectMapper.readValue(
            result.getResponse().getContentAsString(), new TypeReference<>() {
            }
        );

        assertThat(response).hasSize(2);
        assertThat(response).extracting(NotificationResponse::body).containsExactlyInAnyOrder("알림1", "알림2");
        assertThat(response).allMatch(n -> !n.isRead());
    }

    @Test
    void 알림_읽음_처리() {
        Notification notification = notificationRepository.save(createNotification("1", MEMBER_ID, "알림", false));

        setAuthentication();

        MvcTestResult result = mvcTester.patch()
            .uri("/notifications/{notificationId}", notification.getId())
            .exchange();

        assertThat(result).hasStatusOk();

        Notification updated = notificationRepository.findById(notification.getId()).orElseThrow();
        assertThat(updated.getIsRead()).isTrue();
    }

    @Test
    void 알림_읽음_처리_시_권한이_없으면_예외가_발생한다() {
        UUID ownerId = OTHER_MEMBER_ID;
        Notification notification = notificationRepository.save(createNotification("1", ownerId, "타인 알림", false));

        setAuthentication();

        MvcTestResult result = mvcTester.patch()
            .uri("/notifications/{notificationId}", notification.getId())
            .exchange();

        assertThat(result).hasStatus4xxClientError();

        Notification unchanged = notificationRepository.findById(notification.getId()).orElseThrow();
        assertThat(unchanged.getIsRead()).isFalse();
    }

    @Test
    void 모든_알림_읽음_처리() {
        Notification notification1 = notificationRepository.save(createNotification(false));
        Notification notification2 = notificationRepository.save(createNotification(false));

        setAuthentication();

        MvcTestResult result = mvcTester.patch()
            .uri("/notifications")
            .exchange();

        assertThat(result).hasStatusOk();

        em.flush();
        em.clear();

        Notification updated1 = notificationRepository.findById(notification1.getId()).orElseThrow();
        Notification updated2 = notificationRepository.findById(notification2.getId()).orElseThrow();
        assertThat(updated1.getIsRead()).isTrue();
        assertThat(updated2.getIsRead()).isTrue();
    }

    void setAuthentication() {
        MemberDetails principal = new MemberDetails(MEMBER_ID, true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
