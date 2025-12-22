package com.bob.core.member.application.dto.command;

public record ChangePasswordCommand(String oldPassword, String newPassword) {

}
