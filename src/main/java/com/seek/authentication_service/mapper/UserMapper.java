package com.seek.authentication_service.mapper;

import com.seek.authentication_service.dto.request.UserRequest;
import com.seek.authentication_service.dto.response.UserResponse;
import com.seek.authentication_service.model.User;
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
public interface UserMapper {

    User toModel(UserRequest userRequest);

    UserResponse toDto(User user);

    User updateModel(UserRequest userRequest, @MappingTarget User user);
}
