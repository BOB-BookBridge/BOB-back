package com.bob.security.adapter.filter.request;

public record LoginRequest(
    String email,
    String password
) {

}
