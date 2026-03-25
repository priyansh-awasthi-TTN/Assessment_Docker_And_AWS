package com.example.ecommerceproject.util;

import static lombok.AccessLevel.PRIVATE;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = PRIVATE)
public class JwtUtil {

        public static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000L;
        public static final long REFRESH_TOKEN_VALIDITY = 24 * 60 * 60 * 1000L;
        static final long PASSWORD_RESET_TOKEN_VALIDITY = 3600000;
        static final int HMAC_KEY_MIN_BYTES = 32;
        static final String CLAIM_PURPOSE = "purpose";
        static final String PURPOSE_ACCESS = "access";
        static final String PURPOSE_REFRESH = "refresh";
        static final String PURPOSE_PASSWORD_RESET = "password_reset";
        static final String CLAIM_PASSWORD_UPDATED_AT = "pwdUpdatedAt";
        static final String CLAIM_REFRESH_ID = "refreshId";
        final SecretKey key;

        public JwtUtil(@Value("${jwt.secret.key}") String secretKey) {
                byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
                if (keyBytes.length < HMAC_KEY_MIN_BYTES) {
                        throw new IllegalArgumentException("jwt.secret.key must be at least 32 bytes for HS256");
                }
                this.key = Keys.hmacShaKeyFor(keyBytes);
        }

        public String generateToken(
                        Long userId,
                        String email,
                        Collection<? extends GrantedAuthority> roles) {

                List<String> roleNames = roles.stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList();

                return Jwts.builder()
                                .setId(UUID.randomUUID().toString())
                                .setSubject(email)
                                .claim("userId", userId)
                                .claim("roles", roleNames)
                                .claim(CLAIM_PURPOSE, PURPOSE_ACCESS)
                                .setIssuedAt(new Date())
                                .setExpiration(
                                                new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                                .signWith(key)
                                .compact();
        }

        public String generateRefreshToken(Long userId, String email, String refreshTokenId) {
                return Jwts.builder()
                                .setId(UUID.randomUUID().toString())
                                .setSubject(email)
                                .claim("userId", userId)
                                .claim(CLAIM_PURPOSE, PURPOSE_REFRESH)
                                .claim(CLAIM_REFRESH_ID, refreshTokenId)
                                .setIssuedAt(new Date())
                                .setExpiration(
                                                new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                                .signWith(key)
                                .compact();
        }

        public String generatePasswordResetToken(Long userId, String email, long passwordUpdatedAtMillis) {
                return Jwts.builder()
                                .setId(UUID.randomUUID().toString())
                                .setSubject(email)
                                .claim("userId", userId)
                                .claim(CLAIM_PURPOSE, PURPOSE_PASSWORD_RESET)
                                .claim(CLAIM_PASSWORD_UPDATED_AT, passwordUpdatedAtMillis)
                                .setIssuedAt(new Date())
                                .setExpiration(new Date(System.currentTimeMillis() + PASSWORD_RESET_TOKEN_VALIDITY))
                                .signWith(key)
                                .compact();
        }

        public boolean isPasswordResetTokenValid(String token) {
                try {
                        Claims claims = extractAllClaims(token);
                        String purpose = claims.get(CLAIM_PURPOSE, String.class);
                        if (!PURPOSE_PASSWORD_RESET.equals(purpose)) {
                                return false;
                        }
                        return claims.getExpiration().after(new Date());
                } catch (Exception e) {
                        return false;
                }
        }

        public boolean isRefreshTokenValid(String token) {
                Claims claims = extractAllClaims(token);
                String purpose = claims.get(CLAIM_PURPOSE, String.class);
                if (!PURPOSE_REFRESH.equals(purpose)) {
                        return false;
                }
                return claims.getExpiration().after(new Date());
        }

        public String extractJti(String token) {
                return extractAllClaims(token).getId();
        }

        public String extractEmail(String token) {
                return extractAllClaims(token).getSubject();
        }

        public String extractRefreshId(String token) {
                return extractAllClaims(token).get(CLAIM_REFRESH_ID, String.class);
        }

        public Claims extractAllClaims(String token) {

                return Jwts.parserBuilder()
                                .setSigningKey(key)
                                .build()
                                .parseClaimsJws(token)
                                .getBody();
        }

        public Long extractUserId(String token) {

                return extractAllClaims(token)
                                .get("userId", Long.class);
        }

        public boolean isTokenExpired(String token) {

                return extractAllClaims(token)
                                .getExpiration()
                                .before(new Date());
        }

        public boolean validateToken(String token, UserDetails user) {

                Claims claim = extractAllClaims(token);
                String email = claim.getSubject();
                Date expiration = claim.getExpiration();
                String purpose = claim.get(CLAIM_PURPOSE, String.class);

                if (purpose != null && !PURPOSE_ACCESS.equals(purpose)) {
                        return false;
                }

                return email.equals(user.getUsername()) && expiration.after(new Date());
        }

        public boolean isTokenValid(String token) {
                Claims claims = extractAllClaims(token);
                String purpose = claims.get(CLAIM_PURPOSE, String.class);
                if (purpose != null && !PURPOSE_ACCESS.equals(purpose)) {
                        return false;
                }
                return claims.getExpiration().after(new Date());
        }
}
