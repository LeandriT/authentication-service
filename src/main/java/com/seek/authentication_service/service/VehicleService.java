package com.seek.authentication_service.service;

import com.seek.authentication_service.dto.request.VehicleRequest;
import com.seek.authentication_service.dto.response.VehicleResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface VehicleService {
    VehicleResponse create(VehicleRequest vehicleRequest);

    VehicleResponse paid(UUID uuid);

    VehicleResponse update(VehicleRequest vehicleRequest);

    Page<VehicleResponse> index(Pageable pageable);

    VehicleResponse show(UUID uuid);
}
