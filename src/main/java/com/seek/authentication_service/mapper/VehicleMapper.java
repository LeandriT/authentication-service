package com.seek.authentication_service.mapper;

import com.seek.authentication_service.dto.request.VehicleRequest;
import com.seek.authentication_service.dto.response.VehicleResponse;
import com.seek.authentication_service.model.Vehicle;
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
public interface VehicleMapper {

    Vehicle toModel(VehicleRequest request);

    @Mapping(source = "user.uuid", target = "userUuid")
    VehicleResponse toDto(Vehicle entity);

    Vehicle updateModel(VehicleRequest request, @MappingTarget Vehicle entity);
}
