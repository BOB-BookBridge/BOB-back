package com.bob.global.props;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletResponse;

import com.bob.global.utils.web.CookieUtils;

class CookiePropsTest {

    @Test
    void sameSite_값_설정() {
        CookieProps cookieProps = new CookieProps();

        cookieProps.setSameSite("NONE");

        assertThat(cookieProps.getSameSite()).isEqualTo("NONE");

        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieUtils.addCookie(response, "fileName", "value", 1000L);
        assertThat(response.getHeader("Set-Cookie")).contains("SameSite=NONE");
    }
}
