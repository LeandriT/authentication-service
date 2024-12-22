package com.seek.authentication_service.service;

import com.seek.authentication_service.model.RefreshToken;
import com.seek.authentication_service.model.User;
import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken generateRefreshToken(User user);

    Optional<RefreshToken> findByToken(String token);

    RefreshToken validateRefreshToken(RefreshToken token);

    RefreshToken updateRefreshToken(RefreshToken token);
}
