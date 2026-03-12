package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.DAO.UserPrincipal;
import com.SAMUDRA.messaging_system.enums.Role;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;



@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JwtServiceTest {

    private JwtService jwtService;


    private User user;

     private String token ;
    byte[] keyBytes = Decoders.BASE64.decode(
            "V3ryS3cur3D3m0JWTK3yF0rT3st1ngPurp0s3s123456"
    );

    Key key = Keys.hmacShaKeyFor(keyBytes);

     private String expiredToken;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        user = new User();
        user.setId(1L);
        user.setUsername("ram");
        user.setEmail("ram@gmail.com");
        user.setPassword("123");
        user.setProfilePicUrl("oldPic.jpg");
        user.setRole(Role.USER);


        token=jwtService.generateToken(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString()

        );

        expiredToken = Jwts.builder()
                .setSubject("1")
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60))
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // already expired
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    @Test
    void extractUserId_shouldReturnUserId_whenTokenIsValid() {


        // extract userId
        Long extractedUserId = jwtService.extractUserId(token);

        // verify
        assertEquals(user.getId(), extractedUserId);
    }

    @Test
    void extractUserId_shouldThrowException_whenTokenIsInvalid() {

        String invalidToken = "invalid.jwt.token";

        assertThrows(MalformedJwtException.class, () -> {
            jwtService.extractUserId(invalidToken);
        });

        }

        @Test
        void extractUserId_shouldThrowException_whenTokenMalformed() {

            String malformedToken = "abc.def"; // not proper JWT

            assertThrows(MalformedJwtException.class, () -> {
                jwtService.extractUserId(malformedToken);
            });
        }

    @Test
    void extractUserId_shouldThrowException_whenTokenExpired() {

        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.extractUserId(expiredToken);
        });
    }

    @Test
    void generateToken_shouldCreateToken() {

        assertNotNull(token);
    }

    @Test
    void generateToken_shouldContainCorrectUserId() {


        Long extractedId = jwtService.extractUserId(token);

        assertEquals(1L, extractedId);
    }
    @Test
    void generateToken_shouldContainClaims() {



        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtService.getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("ram", claims.get("username"));
        assertEquals("ram@gmail.com", claims.get("email"));
        assertEquals("USER", claims.get("role"));
    }
    @Test
    void generateToken_shouldHaveExpiration() {


        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtService.getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertNotNull(claims.getExpiration());
    }
    @Test
    void generateToken_shouldNotBeExpiredImmediately() {



        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtService.getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void validateToken_shouldReturnTrue_whenTokenValidAndUserMatches() {


        UserPrincipal userPrincipal = new UserPrincipal(user);

        boolean result = jwtService.validateToken(token, userPrincipal);

        assertTrue(result);
    }

    @Test
    void validateToken_shouldReturnFalse_whenUserIdDoesNotMatch() {



        User otherUser = new User();
        otherUser.setId(2L);

        UserPrincipal userPrincipal = new UserPrincipal(otherUser);

        boolean result = jwtService.validateToken(token, userPrincipal);

        assertFalse(result);
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenExpired() {



        UserPrincipal userPrincipal = new UserPrincipal(user);

        boolean result = jwtService.validateToken(expiredToken, userPrincipal);

        assertFalse(result);
    }



    @Test
    void validateToken_shouldReturnFalse_whenTokenSignatureInvalid() {

        // create a token with a different secret key
        byte[] otherKeyBytes = Decoders.BASE64.decode(
                "An0th3rSup3rS3cur3JWTK3yF0rT3st1ng123456789"
        );

        Key otherKey = Keys.hmacShaKeyFor(otherKeyBytes);

        String tamperedToken = Jwts.builder()
                .setSubject("1")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(otherKey, SignatureAlgorithm.HS256)
                .compact();

        UserPrincipal userPrincipal = new UserPrincipal(user);

        boolean result = jwtService.validateToken(tamperedToken, userPrincipal);

        assertFalse(result);
    }

    @Test
    void extractUserId_shouldThrowException_whenTokenNull() {

        assertThrows(IllegalArgumentException.class, () -> {
            jwtService.extractUserId(null);
        });
    }
    @Test
    void extractUserId_shouldThrowException_whenTokenEmpty() {

        String emptyToken = "";

        assertThrows(IllegalArgumentException.class, () -> {
            jwtService.extractUserId(emptyToken);
        });
    }




}