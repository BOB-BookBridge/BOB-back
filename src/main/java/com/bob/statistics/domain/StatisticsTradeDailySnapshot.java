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
public class StatisticsTradeDailySnapshot extends AbstractEntity {

    private LocalDate snapshotDate;

    private long newTrades;

    private long requestedTrades;

    private long acceptedTrades;

    private long rejectedTrades;

    private long canceledTrades;

    private long reservedTrades;

    private long completedTrades;

    public static StatisticsTradeDailySnapshot createEmpty(LocalDate snapshotDate) {
        return StatisticsTradeDailySnapshot.builder()
            .snapshotDate(snapshotDate)
            .newTrades(0)
            .requestedTrades(0)
            .acceptedTrades(0)
            .rejectedTrades(0)
            .canceledTrades(0)
            .reservedTrades(0)
            .completedTrades(0)
            .build();
    }

    public void addNewTrades(long value) {
        newTrades += value;
    }

    public void addRequestedTrades(long value) {
        requestedTrades += value;
    }

    public void addAcceptedTrades(long value) {
        acceptedTrades += value;
    }

    public void addRejectedTrades(long value) {
        rejectedTrades += value;
    }

    public void addCanceledTrades(long value) {
        canceledTrades += value;
    }

    public void addReservedTrades(long value) {
        reservedTrades += value;
    }

    public void addCompletedTrades(long value) {
        completedTrades += value;
    }
}
