package com.seek.authentication_service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seek.authentication_service.dto.base.BaseDto;
import com.seek.authentication_service.model.ParkingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private String plate;
    private String dni;
    private String fullName;
    private String phoneNumber;
    private String secondaryPhoneNumber;
    private Long parkedTime;
    private UUID userUuid;
    private BigDecimal amountCharged;
    private BigDecimal rate;
    private ParkingStatus parkingStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime parkingDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDate;
}