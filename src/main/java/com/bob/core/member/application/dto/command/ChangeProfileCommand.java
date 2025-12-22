package com.bob.core.member.application.dto.command;

import java.util.List;

public record ChangeProfileCommand(
    String nickname, Integer emdId, boolean authenticateArea, Double lat, Double lon,
    List<String> interests
) {

}
