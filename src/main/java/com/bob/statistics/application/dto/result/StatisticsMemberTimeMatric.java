package com.bob.statistics.application.dto.result;

import java.time.LocalDateTime;

public record StatisticsMemberTimeMatric(
    LocalDateTime time,
    long visitors,
    long newMembers,
    long deactivatedMembers,
    long bannedMembers
) {

}
