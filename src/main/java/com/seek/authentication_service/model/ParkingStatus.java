package com.seek.authentication_service.model;

public enum ParkingStatus {
    PARKED("ESTACIONADO"),
    PAID("PAGADO");

    private final String description;

    ParkingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
