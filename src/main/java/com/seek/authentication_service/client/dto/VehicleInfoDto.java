package com.seek.authentication_service.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleInfoDto {
    private String fullName;            // Full name
    private String dni;                 // Identity document (ID)
    private String plate;               // Vehicle plate
    private String brand;               // Vehicle brand
    private String model;               // Vehicle model
    private String year;                // Vehicle year
    private String identificationType; // User identification type
    private String country;             // Country of manufacture
    private String color;               // Vehicle color
    private String serviceType;
    private String vehicleClass;
    private String usageType;
}