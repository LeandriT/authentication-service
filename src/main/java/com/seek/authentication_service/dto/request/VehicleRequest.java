package com.seek.authentication_service.dto.request;

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
    private String licensePlate;
    private String dni;
    private String fullName;
    private String phoneNumber;
    private String secondaryPhoneNumber;
    private Long parkedTime;
    private UUID userUuid;
}