package com.example.demo.service.account_work;

import com.example.demo.interfaces.account_work.JwtInterface;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Clock;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService implements JwtInterface {

    private final Key signingKey;
    private final Clock clock;
    private final Duration accessTtl;
    private final Duration refreshTtl;
    private final String issuer;

    public JwtService(
            @Value("${security.jwt.secret}") String base64Secret,
            @Value("${security.jwt.access-ttl-minutes:60}") long accessTtlMinutes,
            @Value("${security.jwt.refresh-ttl-days:7}") long refreshTtlDays,
            @Value("${security.jwt.issuer:demo-api}") String issuer,
            Clock clock
    ) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.clock = clock;
        this.accessTtl = Duration.ofMinutes(accessTtlMinutes);
        this.refreshTtl = Duration.ofDays(refreshTtlDays);
        this.issuer = issuer;
    }

    @Override
    public String generateToken(String username) {
        return generateAccessToken(username, Map.of());
    }

    @Override
    public String extractUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    public String generateAccessToken(String username, Map<String, Object> extraClaims) {
        var now = clock.instant();
        return Jwts.builder()
                .addClaims(extraClaims)
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(accessTtl)))
                .signWith(signingKey)
                .compact();
    }

    public String generateRefreshToken(String username) {
        var now = clock.instant();
        return Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(refreshTtl)))
                .signWith(signingKey)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getAllClaims(token); // signature & exp verified
            if (issuer != null && !issuer.equals(claims.getIssuer())) return false;
            return claims.getExpiration() != null
                    && claims.getExpiration().after(Date.from(clock.instant()));
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
