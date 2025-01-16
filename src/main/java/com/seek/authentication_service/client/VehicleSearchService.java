package com.seek.authentication_service.client;


import com.seek.authentication_service.client.dto.VehicleInfoDto;
import com.seek.authentication_service.dto.response.infoVehicle.VehicleInfoV2Dto;

public interface VehicleSearchService {


    VehicleInfoDto searchVehicle(String plate);

    VehicleInfoV2Dto searchVehicleV2(String plate);
}