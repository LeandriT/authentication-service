package com.seek.authentication_service.converter;

import com.seek.authentication_service.model.enums.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status status) {
        return status != null ? status.getValue() : null;
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        return dbData != null ? Status.fromValue(dbData) : null;
    }
}