package com.seek.authentication_service.exceptions;

public class RecordAlreadyExistsException extends RuntimeException {
    public RecordAlreadyExistsException(String message) {
        super(message);
    }

    public RecordAlreadyExistsException() {
        super("Record already exists ");
    }
}