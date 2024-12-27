package com.seek.authentication_service.service.impl;

import com.seek.authentication_service.client.VehicleSearchService;
import com.seek.authentication_service.client.dto.VehicleInfoDto;
import com.seek.authentication_service.dto.request.SearchVehicleRequest;
import com.seek.authentication_service.dto.request.VehiclePaidRequest;
import com.seek.authentication_service.dto.request.VehicleRequest;
import com.seek.authentication_service.dto.response.DailyTransactionSummaryDto;
import com.seek.authentication_service.dto.response.DashboardResponse;
import com.seek.authentication_service.dto.response.VehicleResponse;
import com.seek.authentication_service.dto.response.VehicleTransactionLineDto;
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
import com.seek.authentication_service.service.PdfFileGenerator;
import com.seek.authentication_service.service.VehicleService;
import io.github.perplexhub.rsql.RSQLJPASupport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository repository;
    private final UserRepository userRepository;
    private final VehicleMapper mapper;
    private final VehicleSearchService vehicleSearchService;
    private final PdfFileGenerator pdfFileGenerator;

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
        Specification<Vehicle> spec = RSQLJPASupport.toSpecification(search);
        Page<Vehicle> vehiclePage = repository.findAll(spec, pageable);
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
                repository.getTotalMoneyFromVehiclesTodayPerStatus(userUuid, startOfDay, endOfDay, ParkingStatus.PARKED)
                        .stream()
                        .map(vehicle -> {
                            calculateParkingStatus(vehicle); // Calcula el monto estacionado
                            return vehicle.getAmountCalculated(); // Obtiene el monto calculado
                        })// Calcular el monto estacionado
                        .reduce(BigDecimal.ZERO, BigDecimal::add); // Sumar todos los valores
        dashboardResponse.setTotalMoneyFromParkedVehicles(totalMoneyFromParkedVehicles);

        BigDecimal totalMoneyFromPaidVehicles =
                repository.getTotalMoneyFromVehiclesTodayPerStatus(userUuid, startOfDay, endOfDay, ParkingStatus.PAID)
                        .stream()
                        .map(vehicle -> {
                            calculateParkingStatus(vehicle); // Calcula el monto estacionado
                            return vehicle.getAmountCalculated(); // Obtiene el monto calculado
                        })// Calcular el monto estacionado
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboardResponse.setTotalMoneyFromPaidVehicles(totalMoneyFromPaidVehicles);
        return dashboardResponse;
    }

    @Override
    public byte[] generateTotalSummaryToday(UUID userUuid, LocalDate date) {
        log.info("Generando PDF para userUuid: {} y date: {}", userUuid, date);

        User user = userRepository.findById(userUuid).orElseThrow(UserNotFoundException::new);
        String location = user.getLocation().getParentLocation().getName() + " - " + user.getLocation().getName();
        DailyTransactionSummaryDto dailyTransactionSummaryDto = DailyTransactionSummaryDto.builder()
                .user(user.getFullName())
                .rate(user.getRate())
                .location(location)
                .date(date)
                .build();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        String search =
                String.format("user.uuid==%s;parkingDate>='%s';parkingDate<='%s'", userUuid, startOfDay, endOfDay);
        log.info("Consulta RSQL generada: {}", search);
        Specification<Vehicle> spec = RSQLJPASupport.toSpecification(search);
        List<Vehicle> vehicleList = repository.findAll(spec);
        vehicleList.stream().filter(item -> item.getParkingStatus().equals(ParkingStatus.PARKED))
                .forEach(this::calculateParkingStatus);
        // Variables para totales
        BigDecimal totalCalculated = BigDecimal.ZERO;
        BigDecimal totalCharged = BigDecimal.ZERO;

        // Crear el conjunto de líneas de transacciones
        Map<String, List<Vehicle>> groupedVehicles = vehicleList.stream()
                .collect(Collectors.groupingBy(Vehicle::getPlate));

        // Crear el conjunto de líneas de transacciones
        Set<VehicleTransactionLineDto> lines = new HashSet<>();

        // Procesar cada grupo de vehículos
        for (Map.Entry<String, List<Vehicle>> entry : groupedVehicles.entrySet()) {
            String plate = entry.getKey();
            List<Vehicle> vehicles = entry.getValue();

            // Calcular totales por placa
            int vehicleQty = vehicles.size();
            BigDecimal plateTotalCalculated = vehicles.stream()
                    .map(Vehicle::getAmountCalculated)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal plateTotalCharged = vehicles.stream()
                    .map(Vehicle::getAmountCharged)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Long parkedTime = vehicles.stream()
                    .map(Vehicle::getParkedTime)
                    .reduce(0l, Long::sum);

            // Crear el DTO de línea
            VehicleTransactionLineDto line = VehicleTransactionLineDto.builder()
                    .plate(plate)
                    .fullName(vehicles.get(0).getFullName()) // Tomamos el nombre del primer registro
                    .phoneNumber(vehicles.get(0).getPhoneNumber()) // Tomamos el teléfono del primer registro
                    .vehicleQty(vehicleQty)
                    .amountCalculated(plateTotalCalculated)
                    .amountCharged(plateTotalCharged)
                    .parkedTime(parkedTime)
                    .build();

            // Agregar al conjunto de líneas
            lines.add(line);

            // Acumular los totales generales
            totalCalculated = totalCalculated.add(plateTotalCalculated);
            totalCharged = totalCharged.add(plateTotalCharged);
        }

        // Configurar los totales en el DTO principal
        dailyTransactionSummaryDto.setTotalCalculated(totalCalculated);
        dailyTransactionSummaryDto.setTotalCharged(totalCharged);
        dailyTransactionSummaryDto.setTotals(lines);

        log.info("Contenido del DTO: {}", dailyTransactionSummaryDto);
        log.info("Totales Calculados: {}", dailyTransactionSummaryDto.getTotalCalculated());
        log.info("Totales Cargados: {}", dailyTransactionSummaryDto.getTotalCharged());
        log.info("Líneas de Transacción: {}", dailyTransactionSummaryDto.getTotals());
        return pdfFileGenerator.totalToDay(dailyTransactionSummaryDto);
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
