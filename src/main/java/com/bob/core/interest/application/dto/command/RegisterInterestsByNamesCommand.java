package com.bob.core.interest.application.dto.command;

import java.util.List;

public record RegisterInterestsByNamesCommand(List<String> names) {

    public static RegisterInterestsByNamesCommand of(List<String> names) {
        return new RegisterInterestsByNamesCommand(names);
    }
}
