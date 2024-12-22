package com.seek.authentication_service.service.impl;

import com.seek.authentication_service.exceptions.GenericException;
import com.seek.authentication_service.model.RefreshToken;
import com.seek.authentication_service.model.User;
import com.seek.authentication_service.repository.RefreshTokenRepository;
import com.seek.authentication_service.repository.UserRepository;
import com.seek.authentication_service.service.RefreshTokenService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${app.time.extra:31}")
    private Integer days;

    @Override
    public RefreshToken generateRefreshToken(User user) {
        Instant futureHours = Instant.now().plus(days, ChronoUnit.DAYS);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expirationDate(futureHours)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken validateRefreshToken(RefreshToken token) {
        if (token.getExpirationDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            log.info("Refresh Token ya expiro inicie sesion nuevamente {}", token.getToken());
            throw new GenericException(
                    "El token de actualización ha expirado. Por favor, realiza una nueva solicitud de inicio de " +
                            "sesión."
            );
        }
        return token;
    }

    @Override
    public RefreshToken updateRefreshToken(RefreshToken token) {
        Instant futureHours = Instant.now().plus(days, ChronoUnit.DAYS);
        token.setExpirationDate(futureHours);
        refreshTokenRepository.save(token);
        return token;
    }
}
