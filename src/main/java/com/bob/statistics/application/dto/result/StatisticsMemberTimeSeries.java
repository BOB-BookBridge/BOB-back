package com.bob.statistics.application.dto.result;

import java.util.List;

public record StatisticsMemberTimeSeries(
    long visitors,
    long newMembers,
    long deactivatedMembers,
    long bannedMembers,
    List<StatisticsMemberTimeMatric> points
) {

}
