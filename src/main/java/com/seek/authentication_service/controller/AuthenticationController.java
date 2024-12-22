package com.seek.authentication_service.controller;

import com.seek.authentication_service.dto.request.LoginRequest;
import com.seek.authentication_service.dto.request.RefreshTokenRequest;
import com.seek.authentication_service.dto.request.UserRequest;
import com.seek.authentication_service.dto.request.UserUpdateRequest;
import com.seek.authentication_service.dto.response.TokenResponse;
import com.seek.authentication_service.dto.response.UserPasswordResponse;
import com.seek.authentication_service.dto.response.UserResponse;
import com.seek.authentication_service.service.AuthenticationService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequestMapping("/api/users/v1")
@Validated
public class AuthenticationController {

    private final AuthenticationService authService;

    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }


    @GetMapping("/{uuid}")
    public ResponseEntity<UserResponse> show(
            @PathVariable UUID uuid
    ) {
        log.info("Get user: " + uuid);
        UserResponse response = authService.show(uuid);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<UserResponse> showByPhoneNumberAndBirthDay(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("birthDay") LocalDate birthDay
    ) {
        log.info("Get user by phoneNumber {} and birthDay {} ", phoneNumber, birthDay);
        UserResponse response = authService.showByPhoneNumberAndBirthDay(phoneNumber, birthDay);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserResponse> register(
            @RequestBody UserRequest request
    ) {
        log.info("Attempt to register: " + request.getEmail());
        UserResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{uuid}")
    @PermitAll
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID uuid,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        log.info("Attempt to update: " + uuid);
        return new ResponseEntity<>(authService.update(uuid, request), HttpStatus.OK);
    }

    @PostMapping("/reset-password/{uuid}")
    @PermitAll
    public ResponseEntity<UserPasswordResponse> updatePassword(
            @PathVariable UUID uuid
    ) {
        log.info("Attempt to update with id: " + uuid);
        return new ResponseEntity<>(authService.updatePassword(uuid), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody LoginRequest request
    ) {
        log.info("Attempt to login: " + request.getEmail());
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    @PermitAll
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Attempt to generate refresh token");
        return ResponseEntity.ok(authService.validateToken(refreshTokenRequest));
    }
}
