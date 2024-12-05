package com.seek.authentication_service.controller;

import com.seek.authentication_service.dto.response.LocationResponse;
import com.seek.authentication_service.service.LocationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/locations/v1")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @GetMapping()
    public ResponseEntity<List<LocationResponse>> findAll(@RequestParam("location") String location) {
        return ResponseEntity.ok(locationService.findAll(location));
    }

}
