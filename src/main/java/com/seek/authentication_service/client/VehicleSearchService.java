package com.seek.authentication_service.client;


import com.seek.authentication_service.client.dto.VehicleInfoDto;

public interface VehicleSearchService {


    VehicleInfoDto searchVehicle(String plate);
}