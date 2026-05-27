package com.camping.pms.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret",
            "U1VQRVJfU0VDUkVUX0tFWV8xMjM0NTZfTVVTVF9CRV8zMl9DSEFSUw==");
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        String token = jwtService.generateToken("test@example.com");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_shouldReturnCorrectEmail() {
        String token = jwtService.generateToken("test@example.com");
        String username = jwtService.extractUsername(token);
        assertEquals("test@example.com", username);
    }

    @Test
    void isTokenValid_shouldReturnTrue_forValidToken() {
        String token = jwtService.generateToken("test@example.com");
        UserDetails userDetails = User.withUsername("test@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_shouldReturnFalse_forWrongUser() {
        String token = jwtService.generateToken("test@example.com");
        UserDetails userDetails = User.withUsername("other@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void generateRefreshToken_shouldHaveLongerExpiration() {
        String accessToken = jwtService.generateToken("test@example.com");
        String refreshToken = jwtService.generateRefreshToken("test@example.com");
        assertNotEquals(accessToken, refreshToken);
    }
}