package com.seek.authentication_service.service.impl;

import com.seek.authentication_service.dto.request.SearchVehicleRequest;
import com.seek.authentication_service.dto.request.VehiclePaidRequest;
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
import java.util.List;
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
        boolean exist = repository.existsByPlateAndParkingDateAndUserUuid(
                vehicleRequest.getPlate(), LocalDate.now(), vehicleRequest.getUserUuid(), ParkingStatus.PARKED
        );
        if (exist) {
            log.info("Vehicle already registered with UUID: {}", vehicleRequest.getPlate());
            throw new GenericException(String.format("Vehiculo ya registrado, aun no a pagado parqueo placa: %s",
                    vehicleRequest.getPlate()));
        }
        Vehicle model = mapper.toModel(vehicleRequest);
        User user = userRepository.findById(vehicleRequest.getUserUuid()).orElseThrow(UserNotFoundException::new);
        model.setUser(user);
        model.setRate(user.getRate());
        model = repository.save(model);
        return mapper.toDto(model);
    }

    @Override
    public VehicleResponse paid(UUID uuid, VehiclePaidRequest vehiclePaidRequest) {
        Vehicle vehicle = repository.findById(uuid).orElseThrow(VehicleNotFoundException::new);

        if (vehicle.getParkingStatus().equals(ParkingStatus.PAID)) {
            throw new GenericException("Vehicle already paid");
        }
        this.calculateParkingStatus(vehicle);
        vehicle.setAmountCharged(vehicle.getAmountCharged());
        vehicle.setParkingStatus(ParkingStatus.PAID);
        vehicle.setPaymentDate(LocalDateTime.now());
        vehicle = repository.save(vehicle);
        return mapper.toDto(vehicle);
    }


    @Override
    public VehicleResponse update(VehicleRequest vehicleRequest) {
        return null;
    }

    @Override
    public Page<VehicleResponse> index(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public VehicleResponse show(UUID uuid) {
        return null;
    }

    @Override
    public List<VehicleResponse> showByPlate(SearchVehicleRequest searchVehicleRequest) {
        List<Vehicle> vehicles = repository.findByPlateAndParkingDateAndParkingStatus(
                searchVehicleRequest.getPlate(),
                searchVehicleRequest.getDateToSearch(),
                searchVehicleRequest.getUserUuid(),
                ParkingStatus.PARKED
        );

        // Calcular el estado de estacionamiento para cada vehículo
        vehicles.forEach(this::calculateParkingStatus);

        // Convertir las entidades a DTO
        return vehicles.stream().map(mapper::toDto).toList();
    }

    public void calculateParkingStatus(Vehicle vehicle) {
        // Calcular el tiempo en minutos
        long parkedTimeInMinutes = Duration.between(vehicle.getParkingDate(), LocalDateTime.now()).toMinutes();
        vehicle.setParkedTime(parkedTimeInMinutes);

        // Calcular las horas y aplicar la regla de los 15 minutos
        long hoursCharged = parkedTimeInMinutes / 60; // Horas completas
        long remainingMinutes = parkedTimeInMinutes % 60; // Minutos restantes

        // Si los minutos restantes son más de 15, cobrar una hora adicional
        if (remainingMinutes > 15) {
            hoursCharged += 1; // Cobrar una hora adicional
        }

        // Calcular el monto a cobrar
        BigDecimal rate = vehicle.getUser().getRate();
        BigDecimal amountCalculated = rate.multiply(BigDecimal.valueOf(hoursCharged));

        // Actualizar el vehículo
        vehicle.setAmountCalculated(amountCalculated);
        vehicle.setRate(rate);
    }

}
