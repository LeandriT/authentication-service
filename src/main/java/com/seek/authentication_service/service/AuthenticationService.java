package com.seek.authentication_service.service;

import com.seek.authentication_service.dto.request.LoginRequest;
import com.seek.authentication_service.dto.request.UserRequest;
import com.seek.authentication_service.dto.response.TokenResponse;
import com.seek.authentication_service.dto.response.UserResponse;

public interface AuthenticationService {

    UserResponse register(UserRequest request);

    TokenResponse authenticate(LoginRequest request);
}
