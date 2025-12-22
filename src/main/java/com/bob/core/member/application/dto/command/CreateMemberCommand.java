package com.bob.core.member.application.dto.command;

public record CreateMemberCommand(String email, String password, String nickname, Integer emdId) {

}
