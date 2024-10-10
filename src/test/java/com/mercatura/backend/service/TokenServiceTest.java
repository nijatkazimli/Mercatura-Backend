package com.mercatura.backend.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private Authentication authentication;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void generateJWT_ShouldReturnToken() {
        when(authentication.getName()).thenReturn("user");

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        String token = tokenService.generateJWT(authentication);

        assertNotNull(token);
        assertEquals("token", token);
    }

    @Test
    void isJWTExpired_ShouldReturnTrue_WhenExpired() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getExpiresAt()).thenReturn(Instant.now().minusSeconds(3600));
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        boolean isExpired = tokenService.isJWTExpired("token");

        assertTrue(isExpired);
    }

    @Test
    void isJWTExpired_ShouldReturnFalse_WhenNotExpired() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        boolean isExpired = tokenService.isJWTExpired("token");

        assertFalse(isExpired);
    }

    @Test
    void isJWTExpired_ShouldReturnTrue_WhenDecodingFails() {
        when(jwtDecoder.decode(anyString())).thenThrow(new JwtException("Invalid token"));

        boolean isExpired = tokenService.isJWTExpired("invalid-token");

        assertTrue(isExpired);
    }
}
