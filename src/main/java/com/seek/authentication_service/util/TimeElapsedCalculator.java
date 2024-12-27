package com.seek.authentication_service.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeElapsedCalculator {
    private TimeElapsedCalculator() {
    }

    /**
     * Calcula el tiempo transcurrido entre dos fechas y lo devuelve en un formato legible.
     *
     * @param startDateTime Fecha y hora de inicio.
     * @param endDateTime   Fecha y hora de fin.
     * @return Tiempo transcurrido en formato legible (e.g., "1 día 2 horas 15 minutos").
     */
    public static String getElapsedTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }

        Duration duration = Duration.between(startDateTime, endDateTime);

        long days = duration.toDays();
        duration = duration.minusDays(days);

        long hours = duration.toHours();
        duration = duration.minusHours(hours);

        long minutes = duration.toMinutes();

        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(days).append(" día").append(days > 1 ? "s " : " ");
        }
        if (hours > 0) {
            result.append(hours).append(" hora").append(hours > 1 ? "s " : " ");
        }
        if (minutes > 0) {
            result.append(minutes).append(" minuto").append(minutes > 1 ? "s" : "");
        }

        return result.length() > 0 ? result.toString().trim() : "0 minutos";
    }
}
