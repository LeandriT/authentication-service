package com.seek.authentication_service.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleRequest {
    @NotEmpty(message = "placa requerida")
    private String plate;
    private String dni;
    private String fullName;
    private String phoneNumber;
    private String secondaryPhoneNumber;
    private Long parkedTime;
    @NotNull(message = "El UUID del usuario no puede ser nulo")
    private UUID userUuid;
}