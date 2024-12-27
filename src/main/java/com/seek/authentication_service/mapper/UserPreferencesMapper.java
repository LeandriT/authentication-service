package com.seek.authentication_service.mapper;

import com.seek.authentication_service.dto.request.UserPreferencesRequest;
import com.seek.authentication_service.dto.response.UserPreferencesResponse;
import com.seek.authentication_service.model.UserPreferences;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserPreferencesMapper {

    UserPreferences toModel(UserPreferencesRequest request);

    UserPreferencesResponse toDto(UserPreferences entity);

    UserPreferences updateModel(UserPreferencesRequest request, @MappingTarget UserPreferences entity);
}
