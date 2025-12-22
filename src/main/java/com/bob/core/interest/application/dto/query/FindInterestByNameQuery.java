package com.bob.core.interest.application.dto.query;

public record FindInterestByNameQuery(String name) {

    public static FindInterestByNameQuery of(String name) {
        return new FindInterestByNameQuery(name);
    }
}
