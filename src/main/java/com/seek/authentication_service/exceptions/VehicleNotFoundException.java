package com.seek.authentication_service.exceptions;

public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(String message) {
        super(message);
    }

    public VehicleNotFoundException() {
        super("Vehicle not found ");
    }
}