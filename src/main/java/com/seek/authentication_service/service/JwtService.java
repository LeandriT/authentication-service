package com.seek.authentication_service.service;

import com.seek.authentication_service.model.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

public interface JwtService {

    String extractUsername(String token);

    boolean isValid(String token, UserDetails user);

    <T> T extractClaim(String token, Function<Claims, T> resolver);

    String generateToken(User user);

    Claims extractAllClaims(String token);

    boolean isTokenExpired(String token);

    SecretKey getSigninKey();

    Date extractExpiration(String token);
}
