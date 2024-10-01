package com.seek.authentication_service.dto.response;

import com.seek.authentication_service.dto.base.BaseDto;
import com.seek.authentication_service.model.Role;
import java.math.BigDecimal;
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
    private String firstName;
    private String lastName;
    private String username;
    private String phoneNumber;
    private String email;
    private Role role;
    private String city;
    private BigDecimal rate;
}
