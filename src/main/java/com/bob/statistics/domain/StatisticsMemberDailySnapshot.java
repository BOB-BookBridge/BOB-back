package com.bob.statistics.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.shared.entity.AbstractEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StatisticsMemberDailySnapshot extends AbstractEntity {

    private LocalDate snapshotDate;

    private long newMembers;

    private long deactivatedMembers;

    private long bannedMembers;

    public static StatisticsMemberDailySnapshot createEmpty(LocalDate snapshotDate) {
        return StatisticsMemberDailySnapshot.builder()
            .snapshotDate(snapshotDate)
            .newMembers(0)
            .deactivatedMembers(0)
            .bannedMembers(0)
            .build();
    }

    public void addNewMembers(long value) {
        newMembers += value;
    }

    public void addDeactivatedMembers(long value) {
        deactivatedMembers += value;
    }

    public void addBannedMembers(long value) {
        bannedMembers += value;
    }
}
