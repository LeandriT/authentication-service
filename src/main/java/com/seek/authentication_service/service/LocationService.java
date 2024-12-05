package com.seek.authentication_service.service;

import com.seek.authentication_service.dto.response.LocationResponse;
import java.util.List;

public interface LocationService {
    List<LocationResponse> findAll(String location);
}
