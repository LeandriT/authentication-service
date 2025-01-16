package com.seek.authentication_service.event;

import com.seek.authentication_service.client.VehicleSearchService;
import com.seek.authentication_service.client.dto.VehicleInfoDto;
import com.seek.authentication_service.event.dto.CustomEvent;
import com.seek.authentication_service.exceptions.VehicleNotFoundException;
import com.seek.authentication_service.model.Vehicle;
import com.seek.authentication_service.repository.VehicleRepository;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FillVehicleDataEvent {
    private final VehicleRepository repository;
    private final VehicleSearchService vehicleSearchService;

    @Async // Hace que este método se ejecute de forma asíncrona
    @EventListener
    public void handleCustomEvent(CustomEvent event) {
        log.info("Procesando evento: {}", event.getAction());
        Vehicle vehicle = this.repository.findById(event.getUuid()).orElseThrow(VehicleNotFoundException::new);
        this.assignExtraInfoVehicle(vehicle);
        repository.save(vehicle);
    }

    void assignExtraInfoVehicle(Vehicle vehicle) {
        String plate = vehicle.getPlate();
        if (Objects.nonNull(plate) && !plate.isEmpty()) {
            Optional<Vehicle> found = repository.findFirstByPlateAndFullNameIsNotNullOrderByCreatedAtDesc(plate);
            if (found.isEmpty()) {
                boolean validEcuadorianPlate = isValidEcuadorianPlate(plate);
                log.info("Es una placa valida: {}", validEcuadorianPlate);
                if (validEcuadorianPlate) {
                    log.info("Consultando datos placa a SRI: {}", plate);
                    try {
                        VehicleInfoDto vehicleInfoDto = vehicleSearchService.searchVehicle(plate.replace("-", ""));
                        if (Objects.nonNull(vehicleInfoDto)) {
                            log.info("Asignando datos placa V2");
                            vehicle.setBrand(vehicleInfoDto.getBrand());
                            vehicle.setModel(vehicleInfoDto.getModel());
                            vehicle.setModelYear(String.valueOf(vehicleInfoDto.getYear()));
                            vehicle.setManufacturingCountry(vehicleInfoDto.getCountry());
                            vehicle.setFullName(vehicleInfoDto.getFullName());
                            vehicle.setDni(vehicleInfoDto.getDni());
                        }
                    } catch (Exception ex) {
                        log.error("Ocurrio un error al darle fill a extra data vehicle");

                    }
                }
            } else {
                log.info("Asignando datos placa desde base de datos");
                Vehicle vehicleFounded = found.get();
                vehicle.setBrand(vehicleFounded.getBrand());
                vehicle.setModel(vehicleFounded.getModel());
                vehicle.setModelYear(vehicle.getModelYear());
                vehicle.setManufacturingCountry(vehicle.getManufacturingCountry());
                vehicle.setFullName(vehicleFounded.getFullName());
                vehicle.setDni(vehicle.getDni());
            }

        }
    }

    boolean isValidEcuadorianPlate(String plate) {
        if (plate == null || plate.isEmpty()) {
            return false;
        }

        // Patrón para placas de vehículos particulares: ABC-1234
        String vehiclePlatePattern = "^[A-Z]{3}-\\d{3,4}$";
        // Patrón para placas de motocicletas: AB-123A
        String motorcyclePlatePattern = "^[A-Z]{2}-\\d{3}[A-Z]$";

        // Validar contra los patrones
        return Pattern.matches(vehiclePlatePattern, plate) || Pattern.matches(motorcyclePlatePattern, plate);
    }
}
