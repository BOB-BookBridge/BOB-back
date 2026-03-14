package com.bob.statistics.adapter.in;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.bob.statistics.application.port.in.StatisticsVisitorRecorder;

@Configuration
@RequiredArgsConstructor
public class StatisticsVisitorFilterConfig {

    private final StatisticsVisitorRecorder visitorRecorder;

    @Bean
    public StatisticsVisitorRecordFilter statisticsVisitorRecordFilter() {
        return new StatisticsVisitorRecordFilter(visitorRecorder);
    }

    @Bean
    public FilterRegistrationBean<StatisticsVisitorRecordFilter> statisticsVisitorFilterRegistrationBean(
        StatisticsVisitorRecordFilter filter
    ) {
        FilterRegistrationBean<StatisticsVisitorRecordFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
