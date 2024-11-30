package com.seek.authentication_service.controller;

import com.seek.authentication_service.dto.request.SearchVehicleRequest;
import com.seek.authentication_service.dto.request.VehiclePaidRequest;
import com.seek.authentication_service.dto.request.VehicleRequest;
import com.seek.authentication_service.dto.response.VehicleResponse;
import com.seek.authentication_service.service.VehicleService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vehicle")
@RequiredArgsConstructor
@Validated
public class VehicleController {
    private final VehicleService vehicleService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest request) {
        return new ResponseEntity<>(vehicleService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<VehicleResponse>> index(Pageable pageable) {
        return new ResponseEntity<>(vehicleService.index(pageable), HttpStatus.OK);
    }

    @PostMapping("/search-vehicle")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<VehicleResponse>> showByPlate(@RequestBody SearchVehicleRequest searchVehicleRequest) {
        return new ResponseEntity<>(vehicleService.showByPlate(searchVehicleRequest), HttpStatus.OK);
    }

    @PutMapping("/paid")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<VehicleResponse> paid(
            @RequestParam("vehicle_uuid") UUID uuid,
            @RequestBody VehiclePaidRequest vehiclePaidRequest) {
        return new ResponseEntity<>(vehicleService.paid(uuid, vehiclePaidRequest), HttpStatus.OK);
    }
}
