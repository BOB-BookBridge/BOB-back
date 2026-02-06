package com.bob.infrastructure.data.filter.model;

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
public class FilterWord extends AbstractEntity {

    private String keyword;
    private boolean predefined;
    private LocalDateTime createdAt;
}
