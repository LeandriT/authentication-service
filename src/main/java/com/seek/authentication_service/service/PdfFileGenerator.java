package com.seek.authentication_service.service;

import com.seek.authentication_service.dto.response.DailyTransactionSummaryDto;

public interface PdfFileGenerator {
    byte[] totalToDay(DailyTransactionSummaryDto dailyTransactionSummaryDto);
}
