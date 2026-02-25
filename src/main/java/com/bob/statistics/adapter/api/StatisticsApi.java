package com.bob.statistics.adapter.api;

import java.time.LocalDate;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.statistics.adapter.api.request.ReadStatisticsMemberRequest;
import com.bob.statistics.adapter.api.request.ReadStatisticsPostRequest;
import com.bob.statistics.adapter.api.response.StatisticsBasicResponse;
import com.bob.statistics.adapter.api.response.StatisticsMemberResponse;
import com.bob.statistics.adapter.api.response.StatisticsPostResponse;
import com.bob.statistics.application.port.in.StatisticsBasicReader;
import com.bob.statistics.application.port.in.StatisticsMemberReader;
import com.bob.statistics.application.port.in.StatisticsPostReader;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticsApi {

    private final StatisticsBasicReader statisticsBasicReader;
    private final StatisticsMemberReader statisticsMemberReader;
    private final StatisticsPostReader statisticsPostReader;

    @GetMapping("/basic")
    @PreAuthorize("hasRole('ADMIN')")
    public StatisticsBasicResponse readBasic() {
        return StatisticsBasicResponse.of(statisticsBasicReader.readBasic());
    }

    @GetMapping("/members")
    @PreAuthorize("hasRole('ADMIN')")
    public StatisticsMemberResponse readMember(@Valid ReadStatisticsMemberRequest request) {
        LocalDate today = LocalDate.now();
        LocalDate from = request.from() == null ? today : request.from();
        LocalDate to = request.to() == null ? today : request.to();

        return StatisticsMemberResponse.of(statisticsMemberReader.readMember(from, to));
    }

    @GetMapping("/posts")
    @PreAuthorize("hasRole('ADMIN')")
    public StatisticsPostResponse readPost(@Valid ReadStatisticsPostRequest request) {
        LocalDate today = LocalDate.now();
        LocalDate from = request.from() == null ? today : request.from();
        LocalDate to = request.to() == null ? today : request.to();

        return StatisticsPostResponse.of(statisticsPostReader.readPost(from, to));
    }
}
