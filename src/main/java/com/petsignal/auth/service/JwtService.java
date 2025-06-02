package com.petsignal.auth.service; // Adjusted package for workaround

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey; // Changed from java.security.Key
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Estos valores deben ser configurados en application.yml y ser suficientemente seguros.
    // Ejemplo en application.yml:
    // petsignal:
    //   jwt:
    //     secret: ${JWT_SECRET_KEY}
    //     expiration: ${JWT_EXPIRATION_MS}
    //
    // Ejemplo en .env:
    // JWT_SECRET_KEY=your-very-secure-and-long-random-string-for-jwt-at-least-256-bits
    // JWT_EXPIRATION_MS=86400000 # 24 horas en milisegundos

    @Value("${petsignal.jwt.secret}")
    private String jwtSigningKey;

    @Value("${petsignal.jwt.expiration}")
    private long jwtExpiration;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims().add(extraClaims).and() // Use claims().add().and() for chaining
                .subject(userDetails.getUsername()) // .subject() is the new method
                .issuedAt(new Date(System.currentTimeMillis())) // .issuedAt() is the new method
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // .expiration() is the new method
                .signWith(getSigningKey()) // Algorithm is inferred from the key type for HMAC-SHA
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        // Use Jwts.parser().verifyWith(Key).build().parseSignedClaims(String).getPayload()
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() { // Changed return type to SecretKey
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
