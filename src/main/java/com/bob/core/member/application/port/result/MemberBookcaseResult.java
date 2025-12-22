package com.bob.core.member.application.port.result;

public record MemberBookcaseResult(
    Long id, String status, String title, String author,
    String cover, boolean available
) {

}
