package com.bob.support.auth;

import java.util.UUID;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.WithAuthMember;

public class WithAuthMemberExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        WithAuthMember annotation = find(context);
        if (annotation == null) {
            return;
        }

        UUID id = UUID.fromString(annotation.id());
        MemberDetails principal = new MemberDetails(id, true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }

    private WithAuthMember find(ExtensionContext ctx) {
        return ctx.getElement()
            .map(el -> el.getAnnotation(WithAuthMember.class))
            .or(() -> ctx.getTestClass().map(c -> c.getAnnotation(WithAuthMember.class)))
            .orElse(null);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        SecurityContextHolder.clearContext();
    }

}
