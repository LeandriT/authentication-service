package com.seek.authentication_service.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTotalChargedDto {
    private String fullName;
    private BigDecimal totalCharged;
}
