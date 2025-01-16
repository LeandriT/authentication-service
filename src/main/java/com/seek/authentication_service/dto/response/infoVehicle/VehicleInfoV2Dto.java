package com.seek.authentication_service.dto.response.infoVehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VehicleInfoV2Dto {
    private String fullName;            // Full name
    private String dni;                 // Identity document (ID)
    private String plate;               // Vehicle plate
    private String brand;               // Vehicle brand
    private String model;               // Vehicle model
    private String year;                // Vehicle year
    private String identificationType; // User identification type
    private String vehicleType;         // Vehicle type (translated from tipoVehiculo)
    private String country;             // Country of manufacture
    private String color;               // Vehicle color
}
