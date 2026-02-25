package com.bob.statistics.adapter.api.response;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.bob.statistics.application.dto.result.StatisticsMemberTimeMatric;
import com.bob.statistics.application.dto.result.StatisticsMemberTimeSeries;

public record StatisticsMemberResponse(Totals totals, List<Point> points) {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        .withZone(KST);

    public static StatisticsMemberResponse of(StatisticsMemberTimeSeries metrics) {
        return new StatisticsMemberResponse(
            new Totals(metrics.visitors(), metrics.newMembers(), metrics.deactivatedMembers(), metrics.bannedMembers()),
            metrics.points().stream().map(StatisticsMemberResponse::toPoint).toList()
        );
    }

    private static Point toPoint(StatisticsMemberTimeMatric point) {
        return new Point(
            TIME_FORMAT.format(point.time().atZone(KST)),
            point.visitors(),
            point.newMembers(),
            point.deactivatedMembers(),
            point.bannedMembers()
        );
    }

    public record Totals(
        long visit,
        @JsonProperty("new")
        long newCount,
        long deactivated,
        long banned
    ) {

    }

    public record Point(
        String time,
        long visit,
        @JsonProperty("new")
        long newCount,
        long deactivated,
        long banned
    ) {

    }
}
