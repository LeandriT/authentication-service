package com.seek.authentication_service.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "plate")
public class VehicleTransactionLineDto {

    private String plate;
    private String fullName;
    private String phoneNumber;
    private int vehicleQty;
    private BigDecimal amountCalculated;
    private BigDecimal amountCharged;
    private Long parkedTime;

}
