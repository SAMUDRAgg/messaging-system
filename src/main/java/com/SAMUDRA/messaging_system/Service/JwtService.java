package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.Exception.UserException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secretKey = "V3ryS3cur3D3m0JWTK3yF0rT3st1ngPurp0s3s123456"; // 32+ chars

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // ------------------ Generate JWT with username ------------------
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // always username
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ------------------ Extract username from token ------------------
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ------------------ Extract any claim ------------------
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new UserException("JWT token has expired", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new UserException("Unauthorized - Invalid JWT token", HttpStatus.UNAUTHORIZED);
        }
    }

    // ------------------ Validate token ------------------
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
