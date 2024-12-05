package com.seek.authentication_service.model.enums;

public enum Status {
    ACTIVE("ACTIVO"),
    INACTIVE("INACTIVO");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Status fromValue(String value) {
        for (Status status : Status.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}