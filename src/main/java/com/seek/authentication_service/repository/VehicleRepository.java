package com.seek.authentication_service.repository;

import com.seek.authentication_service.model.Vehicle;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    @Query("SELECT v FROM Vehicle v WHERE v.licensePlate = :licensePlate " +
            "AND DATE(v.registrationDate) = :registrationDate " +
            "AND v.user.uuid = :userUuid " +
            "ORDER BY v.registrationDate ASC")
    Optional<Vehicle> findFirstByLicensePlateAndRegistrationDate(
            @Param("licensePlate") String licensePlate,
            @Param("registrationDate") LocalDate registrationDate,
            @Param("userUuid") UUID userUuid
    );

}
