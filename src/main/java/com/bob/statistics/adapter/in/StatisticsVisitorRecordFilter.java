package com.bob.statistics.adapter.in;

import java.io.IOException;
import java.time.LocalDate;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.filter.OncePerRequestFilter;

import com.bob.statistics.application.port.in.StatisticsVisitorRecorder;

@Slf4j
@RequiredArgsConstructor
public class StatisticsVisitorRecordFilter extends OncePerRequestFilter {

    private final StatisticsVisitorRecorder visitorRecorder;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    /* @formatter:off */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            visitorRecorder.record(getClientIp(request), LocalDate.now());
        } catch (Exception e) {
            log.debug("visitor metric record skipped. uri={}", request.getRequestURI(), e);
        }

        filterChain.doFilter(request, response);
    }
    /* @formatter:on */

    private static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank())
            return ip.contains(",") ? ip.split(",")[0].trim() : ip;

        String proxyIp = request.getHeader("Proxy-Client-IP");
        if (proxyIp != null && !proxyIp.isBlank())
            return proxyIp;

        return request.getRemoteAddr();
    }
}
