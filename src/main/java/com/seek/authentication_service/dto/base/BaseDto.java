package com.seek.authentication_service.dto.base;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseDto {

    private UUID uuid;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;

    // Asegúrate de incluir este bloque para la clase Builder
    public static abstract class BaseDtoBuilder<C extends BaseDto, B extends BaseDtoBuilder<C, B>> {
        // Este bloque se necesita para que Lombok pueda crear un Builder
    }
}
