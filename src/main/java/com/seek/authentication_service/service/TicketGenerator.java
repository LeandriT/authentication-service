package com.seek.authentication_service.service;

import com.seek.authentication_service.dto.response.VehicleResponse;

public interface TicketGenerator {
    void generateTicket(VehicleResponse vehicleRequest);
}
