package com.seek.authentication_service.dto.request;


import com.seek.authentication_service.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest implements Serializable {
    @NotEmpty(message = "Firstname not be null")
    private String fullName;
    @NotEmpty(message = "Email is required")
    @Email(message = "Email is not valid")
    private String email;
    private String phoneNumber;
    @NotEmpty(message = "City is required")
    @NotEmpty(message = "Password not be null")
    private String password;
    @NotNull(message = "BirthDay not be null")
    private LocalDate birthDay;

    @NotEmpty(message = "Role not be null")
    private Role role;
    @Builder.Default()
    private String city = "QUITO";
    @NotNull(message = "Rate is required")
    private BigDecimal rate;


}
