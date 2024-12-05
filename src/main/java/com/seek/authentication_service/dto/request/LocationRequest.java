package com.seek.authentication_service.dto.request;

import com.seek.authentication_service.model.enums.Status;
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
public class LocationRequest {
    private UUID uuid;
    private String code;
    private String parentCode;
    private String name;
    private Status status;
}