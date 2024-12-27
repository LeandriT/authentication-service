package com.seek.authentication_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyTransactionSummaryDto {
    private String user;
    private BigDecimal rate;
    private LocalDate date;
    @Builder.Default
    private BigDecimal totalCharged = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal totalCalculated = BigDecimal.ZERO;
    private String location;
    @Builder.Default
    private List<VehicleTransactionLineDto> totals = new ArrayList<>();
}
