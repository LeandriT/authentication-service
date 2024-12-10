package com.seek.authentication_service.service;

import com.seek.authentication_service.dto.request.LoginRequest;
import com.seek.authentication_service.dto.request.UserRequest;
import com.seek.authentication_service.dto.request.UserUpdateRequest;
import com.seek.authentication_service.dto.response.TokenResponse;
import com.seek.authentication_service.dto.response.UserPasswordResponse;
import com.seek.authentication_service.dto.response.UserResponse;
import java.time.LocalDate;
import java.util.UUID;

public interface AuthenticationService {

    UserResponse register(UserRequest request);

    UserResponse update(UUID uuid, UserUpdateRequest request);

    UserPasswordResponse updatePassword(UUID uuid);

    UserResponse show(UUID uuid);

    UserResponse showByPhoneNumberAndBirthDay(String phoneNumber, LocalDate birthDay);

    TokenResponse authenticate(LoginRequest request);

}
