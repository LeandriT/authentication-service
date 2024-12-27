package com.seek.authentication_service.dto.response;

import com.seek.authentication_service.dto.base.BaseDto;
import com.seek.authentication_service.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserPreferencesResponse extends BaseDto {
    private String parameterKey;
    private String parameterValue;
    private Status status;
}