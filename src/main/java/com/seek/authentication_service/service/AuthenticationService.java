package com.seek.authentication_service.service;

import com.seek.authentication_service.dto.request.LoginRequest;
import com.seek.authentication_service.dto.request.UserRequest;
import com.seek.authentication_service.dto.response.TokenResponse;
import com.seek.authentication_service.dto.response.UserResponse;
import java.util.UUID;

public interface AuthenticationService {

    UserResponse register(UserRequest request);

    UserResponse update(UUID uuid, UserRequest request);

    TokenResponse authenticate(LoginRequest request);
}
