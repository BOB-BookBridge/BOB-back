package com.bob.infrastructure.secure.adapter.jwt;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bob.security.application.port.out.TokenManager;

@Component
@RequiredArgsConstructor
public class JwtTokenManager implements TokenManager {

    @Value("${jwt.secret-key}")
    private String key;

    @Value("${jwt.access-token-expire-time}")
    private Long accessExpireTime;

    public String create(String claim) {
        return Jwts.builder()
            .setIssuer("bob")
            .setSubject("access-token")
            .claim("memberId", claim)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + accessExpireTime * 1000))
            .signWith(getSecretKey(key))
            .compact();
    }

    public String create(String claim, Long expireTime) {
        return Jwts.builder()
            .setIssuer("bob")
            .setSubject("access-token")
            .claim("memberId", claim)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expireTime * 1000))
            .signWith(getSecretKey(key))
            .compact();
    }

    public UUID getClaim(String token) {
        return UUID.fromString(Jwts.parserBuilder()
            .setSigningKey(getSecretKey(key))
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("memberId", String.class));
    }

    public boolean verify(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSecretKey(key))
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean expire(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(getSecretKey(key))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private SecretKey getSecretKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
