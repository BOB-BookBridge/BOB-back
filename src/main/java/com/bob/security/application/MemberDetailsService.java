package com.bob.security.application;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bob.security.application.port.dto.AuthMember;
import com.bob.security.application.port.out.MemberLoader;
import com.bob.security.model.MemberDetails;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberLoader memberLoader;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthMember member = memberLoader.load(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));

        return new MemberDetails(
            member.id(), member.email(), member.password(), member.role(),
            member.status().equals("ACTIVE")
        );
    }
}
