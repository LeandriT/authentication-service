package com.seek.authentication_service.exceptions;

public class UserPreferencesNotFoundException extends RuntimeException {
    public UserPreferencesNotFoundException(String message) {
        super(message);
    }

    public UserPreferencesNotFoundException() {
        super("User preferences not found ");
    }
}