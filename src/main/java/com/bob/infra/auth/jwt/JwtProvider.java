package com.bob.infra.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtProvider {

  private static final String ISSUER = "bookswap";

  @Value("${jwt.secret-key}")
  private String key;

  @Value("${jwt.access-token-expire-time}")
  private Long accessExpireTime;

  public String generateAccessToken(String memberId) {
    return Jwts.builder()
        .setIssuer(ISSUER)
        .setSubject("access-token")
        .claim("memberId", memberId)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + accessExpireTime * 1000))
        .signWith(getSecretKey(key))
        .compact();
  }

  public UUID getMemberId(String token) {
    return UUID.fromString(Jwts.parserBuilder()
        .setSigningKey(getSecretKey(key))
        .build()
        .parseClaimsJws(token)
        .getBody()
        .get("memberId", String.class));
  }

  public boolean isVerified(String token) {
    try {
      Jwts.parserBuilder()
          .build()
          .isSigned(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isExpired(String token) {
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
