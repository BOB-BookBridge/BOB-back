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
public class StatisticsPostDailySnapshot extends AbstractEntity {

    private LocalDate snapshotDate;

    private long newPosts;

    private long deletedPosts;

    private long bannedPosts;

    public static StatisticsPostDailySnapshot createEmpty(LocalDate snapshotDate) {
        return StatisticsPostDailySnapshot.builder()
            .snapshotDate(snapshotDate)
            .newPosts(0)
            .deletedPosts(0)
            .bannedPosts(0)
            .build();
    }

    public void addNewPosts(long value) {
        newPosts += value;
    }

    public void addDeletedPosts(long value) {
        deletedPosts += value;
    }

    public void addBannedPosts(long value) {
        bannedPosts += value;
    }
}
