package com.seek.authentication_service.dto.response;

import com.seek.authentication_service.dto.base.BaseDto;
import com.seek.authentication_service.model.Role;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class UserResponse extends BaseDto {
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate birthDay;
    private Role role;
    private String city;
    private BigDecimal rate;
}
