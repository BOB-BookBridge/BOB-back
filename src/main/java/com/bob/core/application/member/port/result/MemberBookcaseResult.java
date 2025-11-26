package com.bob.core.application.member.port.result;

public record MemberBookcaseResult(
    Long id, String status, String title, String author,
    String cover, boolean available
) {

}
