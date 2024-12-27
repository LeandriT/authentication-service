package com.seek.authentication_service.service;

import com.seek.authentication_service.dto.request.UserPreferencesRequest;
import com.seek.authentication_service.dto.response.UserPreferencesResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserPreferencesService {
    UserPreferencesResponse create(UserPreferencesRequest request);

    UserPreferencesResponse update(UUID uuid, UserPreferencesRequest request);

    UserPreferencesResponse findById(UUID uuid);

    List<UserPreferencesResponse> findByUserUuid(UUID userUuid);

    void delete(UUID uuid);

    Page<UserPreferencesResponse> index(Pageable pageable, String search);
}
