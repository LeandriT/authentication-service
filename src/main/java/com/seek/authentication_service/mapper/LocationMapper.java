package com.seek.authentication_service.mapper;

import com.seek.authentication_service.dto.request.LocationRequest;
import com.seek.authentication_service.dto.response.LocationResponse;
import com.seek.authentication_service.model.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
public interface LocationMapper {

    Location toModel(LocationRequest request);

    @Mapping(target = "parentName", source = "parentLocation.name")
    LocationResponse toDto(Location entity);

    Location updateModel(LocationRequest request, @MappingTarget Location entity);
}
