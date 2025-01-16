package com.seek.authentication_service.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.LoadState;
import com.seek.authentication_service.client.VehicleSearchService;
import com.seek.authentication_service.client.dto.VehicleInfoDto;
import com.seek.authentication_service.dto.response.infoVehicle.DataVehicleResponseDto;
import com.seek.authentication_service.dto.response.infoVehicle.VehicleInfoV2Dto;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@Slf4j
public class VehicleSearchServiceImpl implements VehicleSearchService {

    private final WebClient webClient;
    private static final String BASE_URL =
            "https://servicios.axiscloud.ec/AutoServicio/inicio.jsp?ps_empresa=03&ps_accion=P55";
    private static final int RETRY_QTY = 3;

    public VehicleSearchServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://srienlinea.sri.gob.ec").build();
    }

    @Override
    public VehicleInfoDto searchVehicle(String plate) {
        URI uri = URI.create("https://srienlinea.sri.gob.ec/movil-servicios/api/v1.0/matriculacion/valor/" + plate);

        try {
            return webClient.get()
                    .uri(uri)
                    .header("Accept", "application/json, text/plain, */*")
                    .header("Accept-Language", "es,en;q=0.9")
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/json; charset=utf-8")
                    .retrieve()
                    .bodyToMono(VehicleInfoDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error en la respuesta del servidor: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error al realizar la solicitud para la placa {}: {}", plate, e.getMessage());
        }
        return null;
    }

    @Override
    public VehicleInfoV2Dto searchVehicleV2(String plate) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = createBrowser(playwright);
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            page.setDefaultTimeout(84000);

            AtomicReference<VehicleInfoV2Dto> vehicleInfo = new AtomicReference<>();
            AtomicBoolean isSuccess = new AtomicBoolean(false);

            registerResponseListener(page, vehicleInfo, isSuccess);

            int retryCount = 0;
            while (!isSuccess.get() && retryCount < RETRY_QTY) {
                performSearch(page, plate, retryCount);
                retryCount++;
                if (!isSuccess.get()) {
                    waitBeforeRetry(page);
                }
            }

            if (!isSuccess.get()) {
                System.err.println("No fue posible completar la consulta después de varios intentos.");
            }

            browser.close();
            return vehicleInfo.get();
        } catch (Exception e) {
            log.error("Error en Playwright: {}", e.getMessage(), e);
        }
        return null;
    }

    private Browser createBrowser(Playwright playwright) {
        try {
            return playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setTimeout(150000));
        } catch (Exception e) {
            throw new RuntimeException("No se pudo iniciar el navegador Chromium", e);
        }
    }

    private void registerResponseListener(Page page, AtomicReference<VehicleInfoV2Dto> vehicleInfo,
                                          AtomicBoolean isSuccess) {
        page.onResponse(response -> {
            if (response.url().contains("download_construction.jsp")) {
                try {
                    String responseBody = response.text();
                    if (!responseBody.contains("\"lsError\":\"Ha excedido la cantidad de sesiones permitidas\"")) {
                        DataVehicleResponseDto responseDTO = processResponse(responseBody);
                        if (responseDTO != null) {
                            vehicleInfo.set(mapToVehicleInfoDto(responseDTO.getLsInfoPlaca()));
                            isSuccess.set(true);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error al procesar la respuesta: {}", e.getMessage());
                }
            }
        });
    }

    private void performSearch(Page page, String plate, int retryCount) {
        try {
            page.navigate(BASE_URL, new Page.NavigateOptions().setTimeout(42000));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            FrameLocator iframe = page.frameLocator("iframe#iframe_descripcion");
            if (iframe.locator("#filtro_busqueda").isVisible()) {
                iframe.locator("#filtro_busqueda").selectOption("Placa");
                iframe.locator("#valor_busqueda").fill(plate.replace("-", ""));
                iframe.locator("#boton_buscar").click();
                page.waitForTimeout(1500);
            } else {
                throw new PlaywrightException("No se encontró el formulario dentro del iframe.");
            }
        } catch (PlaywrightException e) {
            log.error("Error durante la navegación o interacción: {}", e.getMessage());
        }
    }

    private void waitBeforeRetry(Page page) {
        page.waitForTimeout(2000);
    }

    public DataVehicleResponseDto processResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseBody, DataVehicleResponseDto.class);
        } catch (Exception e) {
            log.error("Error al convertir JSON a objeto: {}", e.getMessage());
        }
        return null;
    }

    private VehicleInfoV2Dto mapToVehicleInfoDto(DataVehicleResponseDto.LsInfoPlaca lsInfoPlaca) {
        if (lsInfoPlaca == null) {
            return null;
        }

        return VehicleInfoV2Dto.builder()
                .fullName(lsInfoPlaca.getNombres())
                .dni(lsInfoPlaca.getIdentificacion())
                .plate(getValue(lsInfoPlaca.getPlaca()))
                .brand(getValue(lsInfoPlaca.getMarca()))
                .model(getValue(lsInfoPlaca.getModelo()))
                .year(getValue(lsInfoPlaca.getAnio()))
                .identificationType(lsInfoPlaca.getTipoIdent())
                .country(getValue(lsInfoPlaca.getPais()))
                .vehicleType(getValue(lsInfoPlaca.getTipoVehiculo()))
                .color(getValue(lsInfoPlaca.getColor1()))
                .build();
    }

    private String getValue(Map<String, DataVehicleResponseDto.LsInfoPlaca.EtiquetaValor> field) {
        if (field != null && field.containsKey("valor")) {
            DataVehicleResponseDto.LsInfoPlaca.EtiquetaValor etiquetaValor = field.get("valor");
            return etiquetaValor != null ? etiquetaValor.getEtiqueta() : null;
        }
        return null;
    }
}