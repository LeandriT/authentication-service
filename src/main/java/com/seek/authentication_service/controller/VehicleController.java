package com.seek.authentication_service.controller;

import com.seek.authentication_service.dto.request.VehicleRequest;
import com.seek.authentication_service.dto.response.VehicleResponse;
import com.seek.authentication_service.service.VehicleService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vehicle")
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> create(@RequestBody VehicleRequest request) {
        return new ResponseEntity<>(vehicleService.create(request), HttpStatus.CREATED);
    }

    @PostMapping("/paid/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> paid(@PathVariable UUID uuid) {
        return new ResponseEntity<>(vehicleService.paid(uuid), HttpStatus.OK);
    }
}
