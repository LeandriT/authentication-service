package com.seek.authentication_service.dto.request;

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
public class UserUpdateRequest implements Serializable {

    @NotEmpty(message = "El nombre completo no puede estar vacío")
    private String fullName;

    @NotEmpty(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no es válido")
    private String email;

    @NotEmpty(message = "La telefono es obligatorio")
    private String phoneNumber;

    @NotEmpty(message = "La contraseña no puede estar vacía")
    private String password;

    @NotNull(message = "La fecha de nacimiento no puede estar vacía")
    private LocalDate birthDay;

    @NotNull(message = "La tarifa es obligatoria")
    private BigDecimal rate;

    @NotNull(message = "La ubicación no puede ser vacía")
    private UUID locationUuid;
}