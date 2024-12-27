package com.seek.authentication_service.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class UserPreferencesRequest {
    @NotNull
    private String parameterKey;

    @NotNull
    private String parameterValue;

    @NotNull
    private UUID userUuid;
}
