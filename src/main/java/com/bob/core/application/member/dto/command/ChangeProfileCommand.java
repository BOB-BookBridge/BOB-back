package com.bob.core.application.member.dto.command;

import java.util.List;

public record ChangeProfileCommand(
    String nickname, Integer emdId, boolean authenticateArea, Double lat, Double lon,
    List<String> interests
) {

}
