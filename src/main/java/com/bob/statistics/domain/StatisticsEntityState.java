package com.bob.statistics.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class StatisticsEntityState extends AbstractEntity {

    private String domain;

    private String entityId;

    private LocalDate cohortDate;

    private String currentStatus;

    private boolean createdCounted;

    private LocalDateTime updatedAt;

    public void update(LocalDate cohortDate, String currentStatus, boolean createdCounted, LocalDateTime updatedAt) {
        this.cohortDate = cohortDate;
        this.currentStatus = currentStatus;
        this.createdCounted = createdCounted;
        this.updatedAt = updatedAt;
    }
}
