package com.seek.authentication_service.dto.response;

import com.seek.authentication_service.dto.base.BaseDto;
import java.math.BigDecimal;
import java.util.UUID;
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
public class VehicleResponse extends BaseDto {
    private String licensePlate;
    private String dni;
    private String fullName;
    private String phoneNumber;
    private String secondaryPhoneNumber;
    private Long parkedTime;
    private UUID userUuid;
    private BigDecimal amountCharged;
    private BigDecimal rate;
}