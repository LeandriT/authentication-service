package com.seek.authentication_service.repository;

import com.seek.authentication_service.model.Vehicle;
import com.seek.authentication_service.model.enums.ParkingStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END " +
            "FROM Vehicle v " +
            "WHERE v.plate = :plate " +
            "AND DATE(v.parkingDate) = :parkingDate " +
            "AND v.parkingStatus = :parkingStatus " +
            "AND v.user.uuid = :userUuid")
    boolean existsByPlateAndParkingDateAndUserUuid(
            @Param("plate") String plate,
            @Param("parkingDate") LocalDate parkingDate,
            @Param("userUuid") UUID userUuid,
            @Param("parkingStatus") ParkingStatus parkingStatus
    );

    @Query("SELECT v FROM Vehicle v " +
            "WHERE v.plate = :plate " +
            "AND DATE(v.parkingDate) = :parkingDate " +
            "AND v.user.uuid = :userUuid " +
            "AND v.parkingStatus = :parkingStatus " +
            "ORDER BY v.parkingDate ASC")
    List<Vehicle> findByPlateAndParkingDateAndParkingStatus(
            @Param("plate") String plate,
            @Param("parkingDate") LocalDate parkingDate,
            @Param("userUuid") UUID userUuid,
            @Param("parkingStatus") ParkingStatus parkingStatus
    );


    @Query("SELECT v FROM Vehicle v WHERE " +
            "(:search IS NULL OR UPPER(v.plate) LIKE CONCAT('%', UPPER(:search), '%'))")
    Page<Vehicle> findByPlateContainingIgnoreCase(@Param("search") String search, Pageable pageable);

}
