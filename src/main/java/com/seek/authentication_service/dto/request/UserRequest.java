package com.seek.authentication_service.dto.request;


import com.seek.authentication_service.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(of = "email")
public class UserRequest implements Serializable {
    @NotEmpty(message = "Firstname not be null")
    private String fullName;
    @NotEmpty(message = "Email is required")
    @Email(message = "Email is not valid")
    private String email;
    private String phoneNumber;
    @NotEmpty(message = "Password not be null")
    private String password;
    @NotNull(message = "BirthDay not be null")
    private LocalDate birthDay;

    @NotEmpty(message = "Role not be null")
    private Role role;
    @NotNull(message = "Rate is required")
    private BigDecimal rate;

    @NotEmpty(message = "location no puede ser vacio")
    private UUID locationUuid;
    @NotNull(message = "Campo Latitud requerido")
    private BigDecimal latitude;
    @NotNull(message = "Campo Longitud requerido")
    private BigDecimal longitude;

}
