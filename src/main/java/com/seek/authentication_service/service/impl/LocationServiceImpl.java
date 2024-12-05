package com.seek.authentication_service.service.impl;

import com.seek.authentication_service.dto.response.LocationResponse;
import com.seek.authentication_service.mapper.LocationMapper;
import com.seek.authentication_service.repository.LocationRepository;
import com.seek.authentication_service.service.LocationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @Override
    public List<LocationResponse> findAll(String location) {
        return locationRepository.findByLocation(location).stream().map(locationMapper::toDto).toList();
    }
}
