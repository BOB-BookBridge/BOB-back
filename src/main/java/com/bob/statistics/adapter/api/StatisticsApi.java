package com.bob.statistics.adapter.api;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.statistics.adapter.api.response.StatisticsBasicResponse;
import com.bob.statistics.application.port.in.StatisticsBasicReader;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticsApi {

    private final StatisticsBasicReader statisticsBasicReader;

    @GetMapping("/basic")
    @PreAuthorize("hasRole('ADMIN')")
    public StatisticsBasicResponse readBasic() {
        return StatisticsBasicResponse.of(statisticsBasicReader.readBasic());
    }
}
