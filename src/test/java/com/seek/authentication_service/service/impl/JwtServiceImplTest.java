package com.seek.authentication_service.service.impl;

import com.seek.authentication_service.model.User;
import com.seek.authentication_service.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private JwtServiceImpl jwtService;

    private String token;
    private UserDetails userDetails;
    private User user;

    @BeforeEach
    void setUp() {
        token = "mockedJwtToken";
        userDetails = mock(UserDetails.class);
        user = new User();
        user.setUsername("testUser");
    }

    @Test
    void testExtractUsername() {
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("testUser");
        JwtServiceImpl spyService = spy(jwtService);
        doReturn(claims).when(spyService).extractAllClaims(anyString());

        String username = spyService.extractUsername(token);
        assertEquals("testUser", username);
    }

    @Test
    void testIsValid_TokenValid() {
        JwtServiceImpl spyService = spy(jwtService);
        when(userDetails.getUsername()).thenReturn("testUser");
        doReturn("testUser").when(spyService).extractUsername(token);
        doReturn(false).when(spyService).isTokenExpired(token);
        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(mockValidToken(false)));

        boolean result = spyService.isValid(token, userDetails);
        assertTrue(result);
    }

    @Test
    void testIsValid_TokenInvalid_LoggedOut() {
        JwtServiceImpl spyService = spy(jwtService);
        when(userDetails.getUsername()).thenReturn("testUser");
        doReturn("testUser").when(spyService).extractUsername(token);
        doReturn(false).when(spyService).isTokenExpired(token);
        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(mockValidToken(true)));

        boolean result = spyService.isValid(token, userDetails);
        assertFalse(result);
    }

    @Test
    void testIsTokenExpired_Expired() {
        JwtServiceImpl spyService = spy(jwtService);
        Date expiredDate = new Date(System.currentTimeMillis() - 1000);
        doReturn(expiredDate).when(spyService).extractExpiration(token);

        boolean isExpired = spyService.isTokenExpired(token);
        assertTrue(isExpired);
    }

    @Test
    void testIsTokenExpired_NotExpired() {
        JwtServiceImpl spyService = spy(jwtService);
        Date futureDate = new Date(System.currentTimeMillis() + 1000);
        doReturn(futureDate).when(spyService).extractExpiration(token);

        boolean isExpired = spyService.isTokenExpired(token);
        assertFalse(isExpired);
    }

    @Test
    void testExtractClaim() {
        Claims claims = mock(Claims.class);
        JwtServiceImpl spyService = spy(jwtService);
        doReturn(claims).when(spyService).extractAllClaims(token);
        when(claims.getSubject()).thenReturn("testUser");

        String subject = spyService.extractClaim(token, Claims::getSubject);
        assertEquals("testUser", subject);
    }

    /*@Test
    void testGenerateToken() {
        JwtServiceImpl spyService = spy(jwtService);
        doReturn(mock(SecretKey.class)).when(spyService).getSigninKey();

        String generatedToken = spyService.generateToken(user);
        assertNotNull(generatedToken);
    }*/
    @Test
    void testGetSigninKey() {
        SecretKey key = jwtService.getSigninKey();
        assertNotNull(key);
    }

    // Método auxiliar para simular un Token válido o no
    private com.seek.authentication_service.model.Token mockValidToken(boolean isLoggedOut) {
        com.seek.authentication_service.model.Token token = new com.seek.authentication_service.model.Token();
        token.setLoggedOut(isLoggedOut);
        return token;
    }
}
