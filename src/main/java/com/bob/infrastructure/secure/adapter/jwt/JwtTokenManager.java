package com.bob.infrastructure.secure.adapter.jwt;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bob.security.application.port.out.infra.TokenManager;

@Component
@RequiredArgsConstructor
public class JwtTokenManager implements TokenManager {

    @Value("${jwt.secret-key}")
    private String key;

    @Value("${jwt.access-token-expire-time}")
    private Long accessExpireTime;

    @Override
    public String create(Map<String, String> claims) {
        return create(claims, accessExpireTime * 1000);
    }

    @Override
    public String create(Map<String, String> claims, Long expiration) {
        JwtBuilder builder = Jwts.builder()
            .setIssuer("bob")
            .setSubject("access-token")
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000));

        claims.forEach(builder::claim);

        return builder
            .signWith(getSecretKey(key))
            .compact();
    }

    @Override
    public Map<String, String> getClaims(String token) {
        Claims body = Jwts.parserBuilder()
            .setSigningKey(getSecretKey(key))
            .build()
            .parseClaimsJws(token)
            .getBody();

        return body.entrySet().stream()
            .filter(entry -> entry.getValue() instanceof String)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> (String)entry.getValue()));
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
