package com.seek.authentication_service.dto;

public class AuthenticationResponse {
    private final String token;
    private final String message;

    public AuthenticationResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}
