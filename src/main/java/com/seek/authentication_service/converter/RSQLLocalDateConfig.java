package com.seek.authentication_service.converter;

import io.github.perplexhub.rsql.RSQLJPASupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RSQLLocalDateConfig {
    private RSQLLocalDateConfig() {
    }

    static {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        RSQLJPASupport.addConverter(LocalDate.class, s -> LocalDate.parse(s, formatter));
    }
}