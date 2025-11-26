package com.bob.global.props;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.bob.support.annotation.ContainerTest;

@DisplayName("쿠키 속성 테스트")
@ContainerTest
@ActiveProfiles("test")
class CookiePropsTests {

    @Nested
    @ActiveProfiles({"dev"})
    class DevProfileTest {

        @Autowired
        CookieProps cookieProps;

        @Test
        void 개발환경_SameSite_NONE() {
            assertThat(cookieProps.getSameSite()).isEqualTo("NONE");
        }
    }

    @Nested
    @ActiveProfiles({"prod"})
    class ProdProfileTest {

        @Autowired
        CookieProps cookieProps;

        @Test
        void 운영환경_SameSite_LAX() {
            assertThat(cookieProps.getSameSite()).isEqualTo("LAX");
        }
    }
}
