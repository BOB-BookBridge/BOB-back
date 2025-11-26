package com.bob.core.application.member.dto.command;

public record CreateMemberCommand(String email, String password, String nickname, Integer emdId) {

}
