package com.example.demo.security.config;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

public class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    public void setup() {
        jwtService = new JwtService();
        userDetails = User.builder()
                .username("testuser")
                .password("testpassword")
                .roles("USER")
                .build();
    }

    @Test
    public void testExtractUsername() {
        String jwt = generateToken();
        String username = jwtService.extractUsername(jwt);
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    public void testGenerateToken() {
        String jwt = jwtService.generateToken(userDetails);
        assertNotNull(jwt);
        assertTrue(jwtService.isTokenValid(jwt, userDetails));
    }

    @Test
    public void testIsTokenValid() {
        String jwt = generateToken();
        assertTrue(jwtService.isTokenValid(jwt, userDetails));
    }

    @Test
    public void testIsTokenInvalid() {
        String jwt = generateToken();
        String modifiedJwt = jwt.replace("testuser","asd");
        assertFalse(jwtService.isTokenValid(modifiedJwt, userDetails));
    }

    private String generateToken() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customClaim", "customValue");
        return jwtService.generateToken(extraClaims, userDetails);
    }
}
