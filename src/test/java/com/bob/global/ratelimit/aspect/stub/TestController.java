package com.bob.global.ratelimit.aspect.stub;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.global.ratelimit.annotation.DisableRateLimit;
import com.bob.global.ratelimit.annotation.RateLimit;

/* Stub Controller */
@RestController
@RequestMapping("/auth")
public class TestController {

    /* custom rate limit */
    @RateLimit(
        name = "test-rate-limit",
        windowSecond = 60, maxRequest = 3,
        target = RateLimit.LimitTarget.IP
    )
    @PostMapping("/rate-limit")
    public ResponseEntity<TestResponse> rateLimitedEndpoint() {
        return ResponseEntity.ok(new TestResponse("success"));
    }

    /* rate limit 비활성 */
    @DisableRateLimit
    @PostMapping("/no-rate-limit")
    public ResponseEntity<TestResponse> noRateLimitEndpoint() {
        return ResponseEntity.ok(new TestResponse("success"));
    }

    /* 전역 설정 */
    @PostMapping("/global-limit")
    public ResponseEntity<TestResponse> globalRateLimitEndpoint() {
        return ResponseEntity.ok(new TestResponse("success"));
    }
}

record TestResponse(String message) {

}
