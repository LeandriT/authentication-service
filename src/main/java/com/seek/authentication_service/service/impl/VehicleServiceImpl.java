package com.seek.authentication_service.service.impl;

import com.seek.authentication_service.client.VehicleSearchService;
import com.seek.authentication_service.client.dto.VehicleInfoDto;
import com.seek.authentication_service.dto.request.SearchVehicleRequest;
import com.seek.authentication_service.dto.request.VehiclePaidRequest;
import com.seek.authentication_service.dto.request.VehicleRequest;
import com.seek.authentication_service.dto.response.DashboardResponse;
import com.seek.authentication_service.dto.response.VehicleResponse;
import com.seek.authentication_service.exceptions.GenericException;
import com.seek.authentication_service.exceptions.UserNotFoundException;
import com.seek.authentication_service.exceptions.VehicleNotFoundException;
import com.seek.authentication_service.mapper.VehicleMapper;
import com.seek.authentication_service.model.Location;
import com.seek.authentication_service.model.User;
import com.seek.authentication_service.model.Vehicle;
import com.seek.authentication_service.model.enums.ParkingStatus;
import com.seek.authentication_service.repository.UserRepository;
import com.seek.authentication_service.repository.VehicleRepository;
import com.seek.authentication_service.service.VehicleService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
    private final VehicleSearchService vehicleSearchService;

    @Override
    public VehicleResponse create(VehicleRequest vehicleRequest) {
        boolean exist = repository.existsByPlateAndParkingDateAndUserUuid(vehicleRequest.getPlate(), LocalDate.now(),
                vehicleRequest.getUserUuid(), ParkingStatus.PARKED);
        if (exist) {
            log.info("Vehicle already registered with UUID: {}", vehicleRequest.getPlate());
            List<Vehicle> vehicleRegistered =
                    repository.findByPlateAndParkingDateAndParkingStatus(vehicleRequest.getPlate(), LocalDate.now(),
                            vehicleRequest.getUserUuid(), ParkingStatus.PARKED);
            vehicleRegistered.forEach(this::calculateParkingStatus);
            throw new GenericException(String.format("Vehiculo ya registrado, aun no a pagado parqueo placa: %s.",
                    vehicleRequest.getPlate()));
        }
        Vehicle vehicle = mapper.toModel(vehicleRequest);
        this.assignExtraInfoVehicle(vehicleRequest.getPlate(), vehicle);
        User user = userRepository.findById(vehicleRequest.getUserUuid()).orElseThrow(UserNotFoundException::new);
        vehicle.setUser(user);
        vehicle.setRate(user.getRate());
        this.assignLocation(vehicle, user);
        vehicle = repository.save(vehicle);
        return mapper.toDto(vehicle);
    }

    @Override
    public VehicleResponse paid(UUID uuid, VehiclePaidRequest vehiclePaidRequest) {
        Vehicle vehicle = repository.findById(uuid).orElseThrow(VehicleNotFoundException::new);

        if (vehicle.getParkingStatus().equals(ParkingStatus.PAID)) {
            final String message = String.format("El Vehiculo ya ha sido pagado, %s", vehicle.getPlate());
            throw new GenericException(message);
        }
        this.calculateParkingStatus(vehicle);
        vehicle.setAmountCharged(vehiclePaidRequest.getAmountCharged());
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
    public Page<VehicleResponse> index(Pageable pageable, String search) {
        Page<Vehicle> vehiclePage = repository.findByPlateContainingIgnoreCase(search, pageable);
        vehiclePage.getContent().forEach(this::calculateParkingStatus);
        return vehiclePage.map(mapper::toDto);
    }

    @Override
    public VehicleResponse show(UUID uuid) {
        return null;
    }

    @Override
    public List<VehicleResponse> showByPlate(SearchVehicleRequest searchVehicleRequest) {
        List<Vehicle> vehicles = repository.findByPlateAndParkingDateAndParkingStatus(searchVehicleRequest.getPlate(),
                searchVehicleRequest.getDateToSearch(), searchVehicleRequest.getUserUuid(), ParkingStatus.PARKED);

        // Calcular el estado de estacionamiento para cada vehículo
        vehicles.forEach(this::calculateParkingStatus);

        // Convertir las entidades a DTO
        return vehicles.stream().map(mapper::toDto).toList();
    }

    @Override
    public DashboardResponse dashboard(UUID userUuid) {
        // DashboardResponse para encapsular los resultados
        DashboardResponse dashboardResponse = new DashboardResponse();
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59, 999000000);
        LocalDateTime now = LocalDateTime.now();
        BigDecimal totalCollectedToday =
                repository.getTotalChargedByUserForCurrentDate(userUuid, startOfDay, now);
        dashboardResponse.setTotalCollectedToday(totalCollectedToday);
        BigDecimal estimatedToBeCollectedToday = this.estimateTotal(totalCollectedToday, startOfDay, now);
        dashboardResponse.setEstimatedToBeCollectedToday(estimatedToBeCollectedToday);

        Long totalVehiclesParkedToday = repository.countVehiclesByUserForCurrentDay(userUuid, startOfDay, endOfDay);
        dashboardResponse.setTotalVehiclesParkedToday(totalVehiclesParkedToday);

        Long totalVehiclesUnpaid =
                repository.countVehiclesByUserPerStatusForToday(userUuid, startOfDay, endOfDay, ParkingStatus.PARKED);
        dashboardResponse.setTotalVehiclesUnpaid(totalVehiclesUnpaid);
        Long totalVehiclesPaid =
                repository.countVehiclesByUserPerStatusForToday(userUuid, startOfDay, endOfDay, ParkingStatus.PAID);
        dashboardResponse.setTotalVehiclesPaid(totalVehiclesPaid);

        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now()
                .withDayOfMonth(LocalDate.now().lengthOfMonth())
                .atTime(23, 59, 59, 999000000);
        BigDecimal totalCollectedMonth =
                repository.getTotalCollectedForCurrentMonth(userUuid, startOfMonth, endOfMonth);
        dashboardResponse.setTotalCollectedMonth(totalCollectedMonth);

        BigDecimal totalMoneyFromParkedVehicles =
                repository.getTotalMoneyFromVehiclesTodayPerStatus(userUuid, startOfDay, endOfDay,
                        ParkingStatus.PARKED);
        dashboardResponse.setTotalMoneyFromParkedVehicles(totalMoneyFromParkedVehicles);

        BigDecimal totalMoneyFromPaidVehicles =
                repository.getTotalMoneyFromVehiclesTodayPerStatus(userUuid, startOfDay, endOfDay, ParkingStatus.PAID);
        dashboardResponse.setTotalMoneyFromPaidVehicles(totalMoneyFromPaidVehicles);
        return dashboardResponse;
    }

    private BigDecimal estimateTotal(BigDecimal totalCharged, LocalDateTime startDare, LocalDateTime endDate) {
        long hoursElapsed = Duration.between(startDare, endDate).toHours();
        if (hoursElapsed == 0) {
            // Evitar división por cero si la hora actual está en la medianoche
            return BigDecimal.ZERO;
        }

        // Aplicar la regla de tres para estimar el total en 24 horas
        return totalCharged
                .multiply(BigDecimal.valueOf(24))
                .divide(BigDecimal.valueOf(hoursElapsed), 2, RoundingMode.HALF_UP);
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

    void assignExtraInfoVehicle(String plate, Vehicle vehicle) {
        if (Objects.nonNull(plate) && !plate.isEmpty()) {
            VehicleInfoDto vehicleInfoDto = vehicleSearchService.searchVehicle(plate.replace("-", ""));
            if (Objects.nonNull(vehicleInfoDto)) {
                vehicle.setBrand(vehicleInfoDto.getMarca());
                vehicle.setModel(vehicleInfoDto.getModelo());
                vehicle.setModelYear(String.valueOf(vehicleInfoDto.getAnioModelo()));
                vehicle.setManufacturingCountry(vehicleInfoDto.getPaisFabricacion());
            }
        }
    }

    void assignLocation(Vehicle vehicle, User user) {
        Location location = user.getLocation();
        vehicle.setLocation(location);
    }

}
