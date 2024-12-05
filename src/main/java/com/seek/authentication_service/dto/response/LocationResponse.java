package com.seek.authentication_service.dto.response;

import com.seek.authentication_service.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationResponse {
    private String uuid;        // Identificador único de la ubicación
    private String code;        // Código único de la localidad
    private String parentCode;  // Código del nivel superior, puede ser NULL
    private String name;        // Nombre de la localidad
    private Status status;      // Estado (ACTIVO, INACTIVO)
}