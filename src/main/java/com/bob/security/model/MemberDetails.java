package com.bob.security.model;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record MemberDetails(
    UUID id,
    String email,
    String password,
    String role,
    boolean enabled
) implements UserDetails {

    public MemberDetails(UUID id, boolean enabled) {
        this(id, null, null, "USER", enabled);
    }

    public MemberDetails(UUID id, String role, boolean enabled) {
        this(id, null, null, role, enabled);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
