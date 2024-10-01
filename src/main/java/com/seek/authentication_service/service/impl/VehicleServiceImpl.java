package com.seek.authentication_service.service.impl;

import com.seek.authentication_service.dto.request.VehicleRequest;
import com.seek.authentication_service.dto.response.VehicleResponse;
import com.seek.authentication_service.exceptions.GenericException;
import com.seek.authentication_service.exceptions.UserNotFoundException;
import com.seek.authentication_service.exceptions.VehicleNotFoundException;
import com.seek.authentication_service.mapper.VehicleMapper;
import com.seek.authentication_service.model.ParkingStatus;
import com.seek.authentication_service.model.User;
import com.seek.authentication_service.model.Vehicle;
import com.seek.authentication_service.repository.UserRepository;
import com.seek.authentication_service.repository.VehicleRepository;
import com.seek.authentication_service.service.VehicleService;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository repository;
    private final UserRepository userRepository;
    private final VehicleMapper mapper;

    @Override
    public VehicleResponse create(VehicleRequest vehicleRequest) {
        Optional<Vehicle> vehicleOptional =
                repository.findFirstByLicensePlateAndRegistrationDate(vehicleRequest.getLicensePlate(), LocalDate.now(),
                        vehicleRequest.getUserUuid());
        if (vehicleOptional.isPresent()) {
            Vehicle vehicle = vehicleOptional.get();
            log.info("Vehicle already registered with UUID: {}", vehicle.getUuid());
            throw new GenericException("Vehicle already registered today");
        }
        Vehicle model = mapper.toModel(vehicleRequest);
        model.setParkingStatus(ParkingStatus.PARKED);
        model.setRegistrationDate(LocalDateTime.now());
        User user = userRepository.findById(vehicleRequest.getUserUuid()).orElseThrow(UserNotFoundException::new);
        model.setUser(user);
        model = repository.save(model);
        return mapper.toDto(model);
    }

    @Override
    public VehicleResponse paid(UUID uuid) {
        Vehicle vehicle = repository.findById(uuid).orElseThrow(VehicleNotFoundException::new);

        if (vehicle.getParkingStatus().equals(ParkingStatus.PAID)) {
            throw new GenericException("Vehicle already paid");
        }

        // Calcular el tiempo en minutos
        long parkedTimeInMinutes = Duration.between(vehicle.getRegistrationDate(), LocalDateTime.now()).toMinutes();
        vehicle.setParkedTime(parkedTimeInMinutes);

        // Calcular las horas y aplicar la regla de los 15 minutos
        long hoursCharged = parkedTimeInMinutes / 60; // Horas completas
        long remainingMinutes = parkedTimeInMinutes % 60; // Minutos restantes

        // Si los minutos restantes son más de 15, cobramos una hora adicional
        if (remainingMinutes > 15) {
            hoursCharged += 1; // Cobrar una hora adicional
        }

        // Calcular el monto a cobrar
        BigDecimal rate = vehicle.getUser().getRate();
        BigDecimal amountCharged = rate.multiply(BigDecimal.valueOf(hoursCharged));

        // Actualizar el vehículo
        vehicle.setAmountCharged(amountCharged);
        vehicle.setParkingStatus(ParkingStatus.PAID);
        vehicle.setRate(rate);
        vehicle = repository.save(vehicle);

        return mapper.toDto(vehicle);
    }


    @Override
    public VehicleResponse update(VehicleRequest vehicleRequest) {
        return null;
    }

    @Override
    public Page<VehicleResponse> index(Pageable pageable) {
        return null;
    }

    @Override
    public VehicleResponse show(UUID uuid) {
        return null;
    }
}
