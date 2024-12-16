package com.seek.authentication_service.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private BigDecimal totalCollectedToday;
    private BigDecimal estimatedToBeCollectedToday;

    private Long totalVehiclesParkedToday;
    private Long totalVehiclesUnpaid;
    private Long totalVehiclesPaid;

    private BigDecimal totalCollectedMonth;
    private BigDecimal totalMoneyFromParkedVehicles;
    private BigDecimal totalMoneyFromPaidVehicles;
}
