package com.seek.authentication_service.service;

import com.seek.authentication_service.dto.request.SearchVehicleRequest;
import com.seek.authentication_service.dto.request.VehiclePaidRequest;
import com.seek.authentication_service.dto.request.VehicleRequest;
import com.seek.authentication_service.dto.response.DashboardResponse;
import com.seek.authentication_service.dto.response.VehicleResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface VehicleService {
    VehicleResponse create(VehicleRequest vehicleRequest);

    VehicleResponse paid(UUID uuid, VehiclePaidRequest vehiclePaidRequest);

    VehicleResponse update(VehicleRequest vehicleRequest);

    Page<VehicleResponse> index(Pageable pageable, String search);

    VehicleResponse show(UUID uuid);

    List<VehicleResponse> showByPlate(SearchVehicleRequest searchVehicleRequest);

    DashboardResponse dashboard(UUID userUuid);

    byte[] generateTotalSummaryToday(UUID userUuid, LocalDate date);
}
