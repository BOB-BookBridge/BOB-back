package com.bob.core.application.member.dto.command;

public record ChangePasswordCommand(String oldPassword, String newPassword) {

}
