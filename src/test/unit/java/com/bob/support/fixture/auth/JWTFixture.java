package com.bob.support.fixture.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JWTFixture {

  public static final String SECRET_KEY = "testsecretkeytestsecretkeytestse";
  public static final Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

  public static String validToken(String memberId) {
    return generateToken(memberId, 60_000);
  }

  public static String expiredToken(String memberId) {
    return generateToken(memberId, -60_000);
  }

  public static String generateToken(String memberId, long durationMillis) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .setIssuer("bookbridge")
        .setSubject("access-token")
        .claim("memberId", memberId)
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + durationMillis))
        .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
        .compact();
  }
}