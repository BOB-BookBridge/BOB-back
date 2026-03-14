package com.bob.statistics.adapter.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

import java.io.IOException;
import java.time.LocalDate;

import jakarta.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.bob.statistics.application.port.in.StatisticsVisitorRecorder;

@ExtendWith(MockitoExtension.class)
@DisplayName("방문자 수집 필터 테스트")
class StatisticsVisitorRecordFilterTest {

    @InjectMocks
    private StatisticsVisitorRecordFilter filter;

    @Mock
    private StatisticsVisitorRecorder visitorRecorder;

    @Test
    void X_FORWARDED_FOR_우선_사용() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/posts");
        request.addHeader("X-Forwarded-For", "203.0.113.10, 10.0.0.5");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        then(visitorRecorder).should().record(eq("203.0.113.10"), any(LocalDate.class));
    }

    @Test
    void X_FORWARDED_FOR가_없으면_PROXY_CLIENT_IP_사용() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/posts");
        request.addHeader("Proxy-Client-IP", "198.51.100.4");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        then(visitorRecorder).should().record(eq("198.51.100.4"), any(LocalDate.class));
    }

    @Test
    void 헤더가_없으면_remoteAddr_사용() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/posts");
        request.setRemoteAddr("127.0.0.10");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        then(visitorRecorder).should().record(eq("127.0.0.10"), any(LocalDate.class));
    }

    @Test
    void OPTIONS_요청_제외() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/statistics/basic");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        then(visitorRecorder).should(never()).record(any(), any());
    }

    @Test
    void 방문자_수집_실패_시_요청_계속_진행() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/posts");
        request.setRemoteAddr("127.0.0.11");
        MockHttpServletResponse response = new MockHttpServletResponse();
        doThrow(new RuntimeException("ignore")).when(visitorRecorder).record(any(), any());

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isNotEqualTo(500);
    }
}
