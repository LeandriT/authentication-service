package com.seek.authentication_service.controller;

import com.seek.authentication_service.dto.request.UserPreferencesRequest;
import com.seek.authentication_service.dto.response.UserPreferencesResponse;
import com.seek.authentication_service.service.UserPreferencesService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-preferences")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('USER')")
public class UserPreferencesController {
    private final UserPreferencesService userPreferencesService;
    private final UserPreferencesService service;

    @GetMapping
    public ResponseEntity<Page<UserPreferencesResponse>> index(
            Pageable pageable,
            @RequestParam(value = "search", defaultValue = "") String search
    ) {
        return new ResponseEntity<>(service.index(pageable, search), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserPreferencesResponse> create(@Valid @RequestBody UserPreferencesRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<UserPreferencesResponse> update(@PathVariable UUID uuid,
                                                          @Valid @RequestBody UserPreferencesRequest request) {
        return ResponseEntity.ok(service.update(uuid, request));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<UserPreferencesResponse> findById(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.findById(uuid));
    }

    @GetMapping("/user/{userUuid}")
    public ResponseEntity<List<UserPreferencesResponse>> findByUserUuid(@PathVariable UUID userUuid) {
        return ResponseEntity.ok(service.findByUserUuid(userUuid));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        service.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
