package com.seek.authentication_service.client.impl;

import com.seek.authentication_service.client.VehicleSearchService;
import com.seek.authentication_service.client.dto.VehicleInfoDto;
import java.net.URI;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

@Service
@Slf4j
public class VehicleSearchServiceImpl implements VehicleSearchService {

    private final WebClient webClient;
    private static final String BASE_URL = "http://localhost:8081"; // Corregido
    private static final int RETRY_QTY = 3;

    public VehicleSearchServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    @Override
    public VehicleInfoDto searchVehicle(String plate) {
        URI uri = URI.create(BASE_URL + "/api/v1/search-by-plate?plate=" + plate);

        try {
            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(VehicleInfoDto.class)
                    .retryWhen(Retry.backoff(RETRY_QTY,
                                    Duration.ofSeconds(2)) // Intentar 3 veces con retraso de 2 segundos
                            .doBeforeRetry(
                                    signal -> log.warn("Reintento debido a error: {}", signal.failure().getMessage()))
                            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                    new RuntimeException("Se agotaron los reintentos para la solicitud",
                                            retrySignal.failure())))
                    .block();
        } catch (Exception e) {
            log.error("Error al realizar la solicitud para la placa {}: {}", plate, e.getMessage(), e);
        }
        return null;
    }

}