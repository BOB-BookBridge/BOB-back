package com.bob.core.area.application.dto.command;

public record MatchAreaQuery(Double lat, Double lon) {

    public static MatchAreaQuery of(Double lat, Double lon) {
        return new MatchAreaQuery(lat, lon);
    }
}
