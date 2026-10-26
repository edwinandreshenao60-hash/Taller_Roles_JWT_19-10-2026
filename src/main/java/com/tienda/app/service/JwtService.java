package com.tienda.app.service;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.tienda.app.entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
@Service
public class JwtService {
    @Value("\u0024{security.jwt.secret-key}") private String secretKey;
    @Value("\u0024{security.jwt.token-expiration}") private Long tokenExpiration;
    private SecretKey signingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String generateToken(Users user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + tokenExpiration);
        return Jwts.builder().claim("userId", user.getId()).claim("rolId", user.getRole().getId())
                .subject(user.getUsername()).issuedAt(now).expiration(expiration)
                .signWith(signingKey()).compact();
    }
    public Claims getClaims(String token) {
        return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
    }
    public boolean isTokenValid(String token) {
        try { getClaims(token); return true; }
        catch (JwtException exception) { return false; }
        catch (IllegalArgumentException exception) { return false; }
    }
    public Integer extractUserId(String token) { return getClaims(token).get("userId", Integer.class); }
    public Integer extractRoleId(String token) { return getClaims(token).get("rolId", Integer.class); }
    public String extractUsername(String token) { return getClaims(token).getSubject(); }
}