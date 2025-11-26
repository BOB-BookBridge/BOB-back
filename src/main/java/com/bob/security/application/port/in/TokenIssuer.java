package com.bob.security.application.port.in;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface TokenIssuer {

    void reissue(HttpServletRequest request, HttpServletResponse response);
}
