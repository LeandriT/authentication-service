package com.seek.authentication_service.client.impl;

import com.seek.authentication_service.client.VehicleSearchService;
import com.seek.authentication_service.client.dto.VehicleInfoDto;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@Slf4j
public class VehicleSearchServiceImpl implements VehicleSearchService {

    private final WebClient webClient;

    public VehicleSearchServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://srienlinea.sri.gob.ec").build();
    }

    @Override
    public VehicleInfoDto searchVehicle(String plate) {
        URI uri = URI.create("https://srienlinea.sri.gob.ec/movil-servicios/api/v1.0/matriculacion/valor/" + plate);

        try {
            // Realizar la solicitud sincrónica y devolver el objeto mapeado
            return webClient.get()
                    .uri(uri)
                    .header("Accept", "application/json, text/plain, */*")
                    .header("Accept-Language", "es,en;q=0.9")
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/json; charset=utf-8")
                    .retrieve()
                    .bodyToMono(VehicleInfoDto.class)
                    .doOnSuccess(vehicleInfo -> {
                        if (vehicleInfo != null) {
                            log.info("Solicitud exitosa para la placa: {}", vehicleInfo.getPlaca());
                        } else {
                            log.warn("La respuesta para la placa {} es nula.", plate);
                        }
                    })
                    .doOnError(error -> {
                        log.error("Error en la solicitud para la placa: {}", plate);
                        log.error("Detalles del error: {}", error.getMessage());
                    })
                    .block(); // Bloquear y devolver el resultado
        } catch (WebClientResponseException e) {
            log.error(
                    "Error en la respuesta del servidor: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("Error al realizar la solicitud: " + e.getMessage());
            return null;
        }
    }

}